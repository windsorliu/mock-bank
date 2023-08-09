# Mock Bank
[English version]()

### Introduction
For an e-Banking Portal you have been given the task to design and implement a reusable REST API for returning the paginated list of money account transactions created in an arbitrary calendar month for a given customer who is logged-on in the portal. For each transaction ‘page’ return the total credit and debit values at the current exchange rate (from the third-party provider). The list of transactions should be consumed from a Kafka topic. Build a Docker image out of the application and prepare the configuration for deploying it to Kubernetes / OpenShift.

### Assumptions
- Every e-banking client has one or more accounts in different currencies (e.g. GBP, EUR, CHF)
- There are approximately one hundred thousand e-banking customers, each with a couple thousands of transactions per month.
- The transactions cover the last ten years and are stored in Kafka with the key being the transaction ID and the value the JSON representation of the transaction
- The user is already authenticated and the API client invoking the transaction API will send a JWT token containing the user’s unique identity key (e.g. P-0123456789)
- The exchange rate on any given date is provided by an external API

For simplicity reasons, consider a money account transaction composed of the following attributes:

- Unique identifier (e.g. 89d3o179-abcd-465b-o9ee-e2d5f6ofEld46)
- Amount with currency (eg GBP 100-, CHF 75)
- Account IBAN (eg. CH93-0000-0000-0000-0000-0)
- Value date (e.g. 01-10-2020)
- Description (e.g. Online payment CHF)

## 環境配置
Java OpenJDK: 17.0.7    
Spring Boot: 3.1.1   
MySQL: 5.7.35  
Kafka: 2.13-3.5.1    
  
### Clone 專案
  
```bash
# 透過 git clone 專案到主機任意路徑下
git clone https://github.com/windsorliu/mock-bank.git
```
  
### 安裝Java

本專案使用Java OpenJDK: 17.0.7，可以參考這些資料進行安裝

- [OpenJDK download](https://jdk.java.net/java-se-ri/17)
- [Download and Install OpenJDK 17](https://www.youtube.com/watch?v=RqIyua9BFQY)

### 安裝MySQL

本專案使用 MySQL: 5.7.35，為避免版本差異問題，建議安裝相同版本  
若您選擇了別的版本，請記得修改`pom.xml`中的配置：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.49</version>
</dependency>
```

- 到[這裡](https://downloads.mysql.com/archives/installer/)點擊5.7.35版，並點擊mysql-installer-community-5.7.35.0.msi(527.8M)的下載
- 參考[此影片](https://www.youtube.com/watch?v=GwHpIl0vqY4)開始下載
- 如果你的IDE可以支援連接資料庫的功能(e.g. IntelliJ IDEA)，在 *Choosing a Setup Type* 可以選擇 Server only
- *Accounts and Roles* 輸入密碼，並記錄下來(建議設計為:casio2988lw201，和`application.properties`中的參數一樣)

### Schema & Data
  
你可以在[這裡]()看到資料庫設計的schema以及data，啟動專案後，請先創建`schema.sql`中的table，並創建`data.sql`中的資料

### 安裝Kafka
  
本專案使用 Kafka: kafka_2.13-3.5.1 版本，請參考以下安裝步驟

- 前往[Apache Kafka](https://kafka.apache.org/downloads)，找到 `Binary downloads: Scala 2.13  - kafka_2.13-3.5.1.tgz`，下載
- 解壓並放置kafka在你想要的位置
- 在 :你的位址\kafka\config，打開server.properties 和 zookeeper.properties，進行調整
```properties
    # server.properties
    # A comma separated list of directories under which to store log files
    log.dirs=:你的位址/kafka/kafka-logs
    # 原本是 log.dirs=/tmp/kafka-logs

    # zookeeper.properties
    # the directory where the snapshot is stored.
    dataDir=:你的位址/kafka/zookeeper-data
    # 原本是 dataDir=/tmp/zookeeper
```
- 在 :你的位址\kafka，打開cmd，輸入 `.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties` 啟動zookeeper，完成後不要關閉
- 在 :你的位址\kafka，打開cmd，輸入 `.\bin\windows\kafka-server-start.bat .\config\server.properties` 啟動server，完成後不要關閉
- 在 :你的位址\kafka\bin\windows，打開cmd，輸入 `kafka-topics.bat --create --bootstrap-server localhost:9092 --topic transaction` ，創建transaction topic，完成後可以關閉
- 如果安裝有遇到問題，可參考[這支影片](https://www.youtube.com/watch?v=BwYFuhVhshI&t=16s)

## 專案介紹
### API文件
我使用了Swagger框架製作API文件，當成功運行這份專案時，輸入此網址`localhost:8080/swagger-ui/index.html#/`，即可在這裡看到關於API的介紹

### 啟動專案

當你配置好了環境(Java,Kafka,MySQL)，且已經執行了`schema.sql`與`data.sql`創建了需要的資料，我們可以開始調用API  
  
> 請注意，當你要調用transaction API的功能前(以及稍後會介紹的自動模擬交易功能前)，要先確保`exchange-rate` table中有匯率資料，也就是需先調用`exchange-rate` API: `GET  /api/exchangerate/update`

### 自動模擬交易

因為在[Assumptions](#assumptions)中有提及`There are approximately one hundred thousand e-banking customers, each with a couple thousands of transactions per month.`  
我想模擬出這樣的交易量，所以我在`Application`中設置了兩個API：

啟動功能： `GET  /api/application/start`  
關閉功能： `GET  /api/application/stop`  
  
用以模擬銀行交易行為：
- 銀行創建user與account  (每5秒創建)
- 使用者交易  (每10秒創建)
- 調用第三方匯率API以更新當前匯率  (每70分鐘創建)
> 第三方匯率我使用[這個服務](https://www.exchangerate-api.com/)，到8/23之前可以調用30000次API(Pro plan)，之後一個月能調用1500次(Free plan)，API key我儲存在`application.properties`

為了方便測試，mysql資料庫儲存 *原始密碼*，當我們要調用user API做登入時可以直接輸入原始密碼登入

### 程式說明
#### User
- 註冊  
  - 檢查email是否已被註冊  
- 登入  
  - 檢查email是否已被註冊  
  - 檢查密碼是否正確  
- 查詢特定用戶的所有帳戶資訊  
  - 檢查用戶是否存在

#### Account
- 創建一個到多個的帳戶
  - 檢查用戶是否存在

#### Transaction
- 創建交易
  - 驗證token
  - 判斷匯款人餘額是否足夠
  - 調用exchange-rate API取得第三方提供的匯率
  - 根據匯率調整匯款人與收款人帳戶餘額
  - 將資料傳送給Kafka Producer
  - 從Kafka Consumer 接收資料
- 查詢特定帳戶的所有交易
  - 檢查帳戶是否存在  
  - 可以分頁返回交易資訊

#### Exchange Rate
- 取得第三方匯率資料
  - 將時區設為`GMT+8`，時間格式調整為`yyyy-MM-dd HH:mm:ss`的方式存入資料庫
- 取得資料庫中最新的匯率資料

#### Util
- UniqueIdentifierGenerator  
  - 提供User Key, Account IBAN, Transaction Key 的唯一識別碼  
- JwtTokenGenerator  
  - 使用HS512加密算法，創建含有User Key資訊的JWT token(過期時間 2 小時)
- UserDataGenerator
  - 生成用戶註冊資料 (給自動模擬交易使用)
- AccountDataGenerator
  - 生成帳戶資料 (給自動模擬交易使用)
- TransactionDataGenerator
  - 生成交易資料 (給自動模擬交易使用)

#### Constant
- Currency
  - 記錄所有貨幣代碼

### 設計決策

- 我使用`GMT+8`作為時區，時間格式為`yyyy-MM-dd HH:mm:ss`，第三方匯率API的時間資料會先處理過才存進資料庫
- 基於效能與日後可能有的複雜查詢考量，我使用Spring JDBC而不是Spring Data JPA去串接資料庫
- [Assumptions](#assumptions)中有提及`The user is already authenticated and the API client invoking the transaction API will send a JWT token containing the user’s unique identity key` ，
目前專案只有在調用transaction API: `POST  /api/transactions` 才需要做JWT token驗證
- 因為user都是被驗證過的用戶，只要是token過期了便會自動生成新的token給予該user
- 目前這些API在被調用時並不會捕捉以下的錯誤情況：

```markdown
account:  `POST  /api/accounts`  
- 不存在的貨幣  
- 餘額為負  
- 餘額超出account table中的balance欄位的限制(DECIMAL(12, 2))
  
transaction:  `POST  /api/transactions`  
- 餘額為負  
- 貨幣不存在  
- 不存在的匯款人帳號  
- 不存在的收款人帳號  
- 收款人餘額超出account table中的balance欄位的限制(DECIMAL(12, 2))  
```
  
### 專案後期改進
  
- 使用Spring Security去保護每個API端點，創建User和Admin等角色，調整每個API可以被調用的權限
- 針對每個功能的Controller層去使用MockMvc去做單元測試(因為時間問題，尚未完成)
- 使用Docker, Kubernetes, CircleCI等技術部署專案
