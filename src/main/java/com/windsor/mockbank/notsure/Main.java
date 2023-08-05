package com.windsor.mockbank.notsure;

import com.windsor.mockbank.constant.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        // 假設這是您的匯率表
        Map<String, BigDecimal> conversionRates = Map.of(
                "USD", new BigDecimal("1"),
                "JPY", new BigDecimal("139.6680"),
                "TWD", new BigDecimal("31.0480")
        );

        // remitter 的帳戶貨幣是 TWD，payee 的帳戶貨幣是 JPY
        String remitterCurrency = "TWD";
        String payeeCurrency = "JPY";

        // remitter 想要匯款的金額是 1000 JPY
        BigDecimal remitAmountJPY = new BigDecimal("1000");

        // 取得 remitter 幣值的匯率
        BigDecimal remitterRate = conversionRates.get(remitterCurrency);

        // 取得 payee 幣值的匯率
        BigDecimal payeeRate = conversionRates.get(payeeCurrency);

        // 計算 remitter 匯款給 payee 的金額
        BigDecimal payeeAmount = remitAmountJPY.multiply(remitterRate).divide(payeeRate, 2, BigDecimal.ROUND_HALF_UP);

        System.out.println("Remitter 應匯款 " + payeeAmount + " " + payeeCurrency);

    }
}
