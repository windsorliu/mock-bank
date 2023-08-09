package com.windsor.mockbank.util;

import java.util.UUID;

public class UniqueIdentifierGenerator {

    public static String generateTransactionKey() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        // 移除連字符
        String withoutHyphens = uuidString.replace("-", "");

        // 插入指定的分隔符
        StringBuilder transactionKey = new StringBuilder();
        int[] dashPositions = {8, 12, 16, 20};
        int startIndex = 0;
        for (int position : dashPositions) {
            transactionKey.append(withoutHyphens, startIndex, position).append("-");
            startIndex = position;
        }
        transactionKey.append(withoutHyphens, startIndex, withoutHyphens.length());

        return transactionKey.toString();
    }

    public static String generateUserKey() {
        // 生成UUID
        UUID uuid = UUID.randomUUID();

        // 移除連字符，取前10位
        String numbers = uuid.toString().replaceAll("-", "").substring(0, 10);

        // 添加前綴 "P-"
        String userKey = "P-" + numbers;

        return userKey;
    }

    public static String generateAccountIBAN() {
        // 生成UUID
        UUID uuid = UUID.randomUUID();

        // 取前23位，包含連字符
        String numbers = uuid.toString().substring(0, 23);

        // 添加前缀 "P-"
        String accountIBAN = "IBAN-" + numbers;

        return accountIBAN;
    }
}
