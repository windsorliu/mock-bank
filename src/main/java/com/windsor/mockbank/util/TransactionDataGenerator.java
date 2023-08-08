package com.windsor.mockbank.util;

import com.windsor.mockbank.constant.Currency;
import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class TransactionDataGenerator {
    @Autowired
    private AccountDao accountDao;

    public Transaction generateTransaction() {
        // 選擇 匯款人(remitter) 和 收款人(payee) 帳戶
        List<Integer> AccountIdList = accountDao.getAccountIdList();
        Random random = new Random();
        Integer remitterAccountId;
        Integer payeeAccountId;

        while (true) {
            remitterAccountId = AccountIdList.get(random.nextInt(AccountIdList.size()));
            payeeAccountId = AccountIdList.get(random.nextInt(AccountIdList.size()));

            if (!remitterAccountId.equals(payeeAccountId)) {
                break;
            }
        }

        Account remitterAccount = accountDao.getAccountById(remitterAccountId);
        Account payeeAccount = accountDao.getAccountById(payeeAccountId);

        // 選擇 匯款貨幣
        Currency[] currencies = Currency.values();
        Currency randomCurrency = currencies[random.nextInt(currencies.length)];
        String currency=randomCurrency.name();

        // 選擇 匯款金額
        int randomInt = random.nextInt(100000) + 1;  // 交易金額範圍1~100000
        BigDecimal amount = BigDecimal.valueOf(randomInt);

        // 創建transaction資訊
        Transaction transaction = new Transaction();
        transaction.setRemitterAccountIBAN(remitterAccount.getAccountIBAN());
        transaction.setPayeeAccountIBAN(payeeAccount.getAccountIBAN());
        transaction.setAmount(amount);
        transaction.setCurrency(currency);

        return transaction;
    }
}
