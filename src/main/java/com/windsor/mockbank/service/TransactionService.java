package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.ExchangeRateDao;
import com.windsor.mockbank.dao.TransactionDao;
import com.windsor.mockbank.dto.TransactionQueryParams;
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
import java.math.RoundingMode;
import java.util.List;
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
        // 取得 匯款帳戶貨幣 與 交易貨幣 的 匯率
        Account remitterAccount = accountDao.getAccountByIBAN(transaction.getRemitterAccountIBAN());
        ExchangeRate exchangeRate = exchangeRateDao.getLatestData();

        String remitterCurrency = remitterAccount.getCurrency();
        String transactionCurrency = transaction.getCurrency();

        BigDecimal remitterRate = getRate(exchangeRate, remitterCurrency);
        BigDecimal transactionRate = getRate(exchangeRate, transactionCurrency);

        // 將 匯款帳戶餘額對應調整成交易匯率
        BigDecimal remitterBalance = remitterAccount.getBalance().multiply(transactionRate).divide(remitterRate, 2, RoundingMode.HALF_EVEN);

        // 如果餘額小於這次的交易金額
        if (remitterBalance.compareTo(transaction.getAmount()) < 0) {
            log.warn("The account: {} does not have sufficient balance for the transaction"
                    , remitterAccount.getAccountIBAN());

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return UniqueIdentifierGenerator.generateTransactionKey();
    }

    public void createTransaction(Transaction transaction) {
        // 取得 匯款帳戶、收款帳戶
        Account remitterAccount = accountDao.getAccountByIBAN(transaction.getRemitterAccountIBAN());
        Account payeeAccount = accountDao.getAccountByIBAN(transaction.getPayeeAccountIBAN());

        // 取得 本次交易的貨幣
        String remitterCurrency = remitterAccount.getCurrency();
        String payeeCurrency = payeeAccount.getCurrency();
        String transactionCurrency = transaction.getCurrency();

        // 取得 本次交易的貨幣匯率
        ExchangeRate exchangeRate = exchangeRateDao.getLatestData();

        BigDecimal remitterRate = getRate(exchangeRate, remitterCurrency);
        BigDecimal payeeRate = getRate(exchangeRate, payeeCurrency);
        BigDecimal transactionRate = getRate(exchangeRate, transactionCurrency);

        // 調整 匯款帳戶、收款帳戶的餘額
        BigDecimal remitterAmount = transaction.getAmount().multiply(remitterRate).divide(transactionRate, 2, RoundingMode.HALF_EVEN);
        BigDecimal payeeAmount = transaction.getAmount().multiply(payeeRate).divide(transactionRate, 2, RoundingMode.HALF_EVEN);

        BigDecimal remitterBalance = remitterAccount.getBalance().subtract(remitterAmount);
        BigDecimal payeeBalance = payeeAccount.getBalance().add(payeeAmount);

        accountDao.updateBalance(remitterBalance, transaction.getRemitterAccountIBAN());
        accountDao.updateBalance(payeeBalance, transaction.getPayeeAccountIBAN());

        transactionDao.createTransaction(transaction);
    }

    private BigDecimal getRate(ExchangeRate exchangeRate, String currency) {
        Map<String, BigDecimal> conversionRates = exchangeRate.getConversionRates();
        Number number = conversionRates.get(currency);
        BigDecimal rate = new BigDecimal(number.toString());

        return rate;
    }

    public void isAccountExist(String accountIBAN) {
        Account account = accountDao.getAccountByIBAN(accountIBAN);

        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<Transaction> getTransactions(TransactionQueryParams transactionQueryParams) {
        return transactionDao.getTransactions(transactionQueryParams);
    }

    public Integer countTransactions(String accountIBAN) {
        return transactionDao.countTransactions(accountIBAN);
    }
}
