package com.windsor.mockbank.constant;

import java.util.Random;



public class Main {
    public static void main(String[] args) {
        Random random = new Random();
        Currency[] currencies = Currency.values();
        Currency randomCurrency = currencies[random.nextInt(currencies.length)];
        System.out.println(randomCurrency.name());
    }
}
