package com.example.petstore.utils;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
