package com.windsor.mockbank.util;

import java.util.UUID;

public class UniqueIdentifierGenerator {

    public static String generateTransactionUniqueIdentifier() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        // 移除连字符
        String withoutHyphens = uuidString.replace("-", "");

        // 插入指定的分隔符
        StringBuilder uniqueIdentifier = new StringBuilder();
        int[] dashPositions = {8, 12, 16, 20};
        int startIndex = 0;
        for (int position : dashPositions) {
            uniqueIdentifier.append(withoutHyphens, startIndex, position).append("-");
            startIndex = position;
        }
        uniqueIdentifier.append(withoutHyphens, startIndex, withoutHyphens.length());

        return uniqueIdentifier.toString();
    }

    public static String generateUserUniqueIdentityKey() {
        // 生成UUID
        UUID uuid = UUID.randomUUID();

        // 移除连字符，并截取前10位数字
        String numbers = uuid.toString().replaceAll("-", "").substring(0, 10);

        // 添加前缀 "P-"
        String uniqueKey = "P-" + numbers;

        return uniqueKey;
    }
}
