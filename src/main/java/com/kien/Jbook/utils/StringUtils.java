package com.kien.Jbook.utils;

public class StringUtils {

    /**
     * スネークケース（例: sample_text）をキャメルケース（例: sampleText）に変換する
     * SQLエラーメッセージから特定のカラム名を取得する時に使用できる
     * @param input
     * @return
     */
    public static String toCamelCase(String input) {
        if (input == null) {
            return null;
        }
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                result.append(word);
            } else {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
