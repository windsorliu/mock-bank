# Mock Bank
[中文版本]()

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

## Environment Setup
Java OpenJDK: 17.0.7  
Spring Boot: 3.1.1  
MySQL: 5.7.35  
Kafka: 2.13-3.5.1

### Clone the Project

```bash
# Clone the project to any directory on your pc
git clone https://github.com/windsorliu/mock-bank.git
```

### Install Java

This project uses Java OpenJDK: 17.0.7. You can refer to these resources for installation:

- [OpenJDK download](https://jdk.java.net/java-se-ri/17)
- [Download and Install OpenJDK 17](https://www.youtube.com/watch?v=RqIyua9BFQY)

### Install MySQL

This project uses MySQL: 5.7.35. To avoid version conflicts, it's recommended to install the same version. If you choose a different version, remember to modify the version in the `pom.xml`:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.49</version>
</dependency>
```

- Go to [this link](https://downloads.mysql.com/archives/installer/) and click on version 5.7.35 to download `mysql-installer-community-5.7.35.0.msi` (527.8MB).
- Follow [this video](https://www.youtube.com/watch?v=GwHpIl0vqY4) to begin the installation.
- If your IDE supports database connections (e.g. IntelliJ IDEA), you can choose "Server only" under *Choosing a Setup Type*.
- Set the password at *Accounts and Roles* (Suggested design: casio2988lw201, consistent with the parameters in the `application.properties`).

### Schema & Data

You can see the schema design and data in [this link](). After starting the project, create the tables defined in `schema.sql` and insert the data from `data.sql`.

### Install Kafka

This project uses Kafka: kafka_2.13-3.5.1. Follow these steps to install it:

- Visit [Apache Kafka](https://kafka.apache.org/downloads) and find the `Binary downloads: Scala 2.13 - kafka_2.13-3.5.1.tgz`. Download it.
- Extract and place Kafka in your desired location.
- In `:your_path\kafka\config`, open `server.properties` and `zookeeper.properties` for adjustment:
```properties
    # server.properties
    # A comma separated list of directories under which to store log files
    log.dirs=:your_path/kafka/kafka-logs
    # Original: log.dirs=/tmp/kafka-logs

    # zookeeper.properties
    # the directory where the snapshot is stored.
    dataDir=:your_path/kafka/zookeeper-data
    # Original: dataDir=/tmp/zookeeper
```
- In `:your_path\kafka`, open cmd and enter `.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties` to start Zookeeper. Do not close it after completion.
- In `:your_path\kafka`, open cmd and enter `.\bin\windows\kafka-server-start.bat .\config\server.properties` to start the Kafka server. Do not close it after completion.
- In `:your_path\kafka\bin\windows`, open cmd and enter `kafka-topics.bat --create --bootstrap-server localhost:9092 --topic transaction` to create the `transaction` topic. You can close the cmd after completion.
- If you encounter any issues during installation, refer to [this video](https://www.youtube.com/watch?v=BwYFuhVhshI).

## Project Overview
### API Documentation
Swagger framework is used to generate API documentation. After successfully running the project, visit `localhost:8080/swagger-ui/index.html#/` to access the API documentation.

### Running the Project

Once you have set up your environment (Java, Kafka, MySQL) and created the necessary data using `schema.sql` and `data.sql`, you can start using the API.

> Please note that before using the transaction API (and the automatic transaction simulation feature described later), ensure that the `exchange-rate` table contains exchange rate data by calling the `GET /api/exchangerate/update` API.

### Automatic Transaction Simulation

As mentioned in [Assumptions](#assumptions), `There are approximately one hundred thousand e-banking customers, each with thousands of transactions per month.` I have implemented two APIs for simulating this amount of transactions:

Start Simulation: `GET /api/application/start`
Stop Simulation: `GET /api/application/stop`

These APIs simulate banking transactions behavior:
- Creating users and accounts in the bank (every 5 seconds)
- Simulating user transactions (every 10 seconds)
- Updating the current exchange rate by calling a third-party API (every 70 minutes)
> I'm using [this service](https://www.exchangerate-api.com/) for third-party exchange rate data. It's free for up to 30,000 API calls per month until August 23rd (Pro plan), then 1,500 API calls per month (Free plan). The API key is stored in `application.properties`.

For testing convenience, the MySQL database stores the *original passwords*. When calling the user login API, you can directly input the original password to log in.

### Program Explanation
#### User
- SignUp
  - Checks if the email is already registered.
- Login
  - Checks if the email is registered.
  - Checks if the password is correct.
- Retrieve all account information for a specific user
  - Checks if the user exists.

#### Account
- Create one or multiple accounts
  - Checks if the user exists.

#### Transaction
- Create a transaction
  - Validates the token.
  - Checks if the remitter's balance is sufficient.
  - Calls the exchange rate API to get the third-party exchange rate.
  - Adjusts remitter and payee account balances based on the exchange rate.
  - Sends data to Kafka Producer.
  - Receives data from Kafka Consumer.
- Retrieve all transactions for a specific account
  - Checks if the account exists.
  - Supports paginated response.

#### Exchange Rate
- Retrieve third-party exchange rate data
  - Sets timezone to GMT+8 and stores time data in the format `yyyy-MM-dd HH:mm:ss`.
- Retrieve the latest exchange rate data from the database.

#### Util
- UniqueIdentifierGenerator
  - Generates unique identifiers for User Key, Account IBAN, and Transaction Key.
- JwtTokenGenerator
  - Creates a JWT token with User Key information using the HS512 encryption algorithm (expires in 2 hours).
- UserDataGenerator
  - Generates user registration data (for automatic transaction simulation).
- AccountDataGenerator
  - Generates account data (for automatic transaction simulation).
- TransactionDataGenerator
  - Generates transaction data (for automatic transaction simulation).

#### Constant
- Currency
  - Stores all currency codes.

### Design Decisions

- I use GMT+8 as the timezone and `yyyy-MM-dd HH:mm:ss` as the time format. Time data from the third-party exchange rate API is processed before being stored in the database.
- For performance and potential complex queries in the future, I use Spring JDBC instead of Spring Data JPA to interact with the database.
- As mentioned in [Assumptions](#assumptions), `The user is already authenticated, and the API client invoking the transaction API will send a JWT token containing the user’s unique identity key.` Currently, JWT token validation is only required when calling the transaction API: `POST /api/transactions`.
- Since users are already authenticated, when a token expires, a new token is automatically generated for the user.
- Currently, these APIs do not capture the following error scenarios when invoked:

```markdown
account:  `POST /api/accounts`
- Nonexistent currency
- Negative balance
- Balance exceeding the limit of the `balance` column in the `account` table (DECIMAL(12, 2))

transaction:  `POST /api/transactions`
- Negative balance
- Nonexistent currency
- Nonexistent remitter account
- Nonexistent payee account
- Receiver balance exceeding the limit of the `balance` column in the `account` table (DECIMAL(12, 2))
```

### Project Enhancements

- Use Spring Security to protect each API endpoint, create roles such as User and Admin, and adjust the permissions for each API invocation.
- Use MockMvc for unit testing in the Controller layer for each feature (not implemented due to time constraints).
- Deploy the project using technologies like Docker, Kubernetes, and CircleCI.