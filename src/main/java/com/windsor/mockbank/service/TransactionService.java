package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.ExchangeRateDao;
import com.windsor.mockbank.dao.TransactionDao;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class TransactionService {

    private final static Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ExchangeRateDao exchangeRateDao;

    public String getTransactionKey(Transaction transaction) {
        Account remitterAccount = accountDao.getAccountByIBAN(transaction.getRemitterAccountIBAN());

        // 如果餘額不足
        if (remitterAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            log.warn("The account: {} does not have " +
                            "sufficient balance for the transaction."
                    , remitterAccount.getAccountIBAN());

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return UniqueIdentifierGenerator.generateTransactionKey();
    }

    public void createTransaction(Transaction transaction) {
        // call 匯率API
        ExchangeRate exchangeRate = exchangeRateDao.getLatestData();
        Map<String, BigDecimal> conversionRates = exchangeRate.getConversionRates();

        // 取得 匯款帳戶、收款帳戶
        Account remitterAccount = accountDao.getAccountByIBAN(transaction.getRemitterAccountIBAN());
        Account payeeAccount = accountDao.getAccountByIBAN(transaction.getPayeeAccountIBAN());

        // 取得 本次交易的貨幣
        String remitterCurrency = remitterAccount.getCurrency();
        String payeeCurrency = payeeAccount.getCurrency();
        String transactionCurrency = transaction.getCurrency();

        // 調整 匯款帳戶、收款帳戶的餘額
        BigDecimal remitterRate = conversionRates.get(remitterCurrency);
        BigDecimal payeeRate = conversionRates.get(payeeCurrency);
        BigDecimal transactionRate = conversionRates.get(transactionCurrency);

        BigDecimal remitterAmount = transaction.getAmount().multiply(remitterRate).divide(transactionRate, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal payeeAmount = transaction.getAmount().multiply(payeeRate).divide(transactionRate, 2, BigDecimal.ROUND_HALF_UP);

        BigDecimal remitterBalance = remitterAccount.getBalance().subtract(remitterAmount);
        BigDecimal payeeBalance = payeeAccount.getBalance().add(payeeAmount);

        accountDao.updateBalance(remitterBalance, transaction.getRemitterAccountIBAN());
        accountDao.updateBalance(payeeBalance, transaction.getPayeeAccountIBAN());

        transactionDao.createTransaction(transaction);
    }
}
