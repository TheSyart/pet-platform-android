package com.example.petstore.utils;

import java.util.List;

public class CheckNull {
    public boolean areFieldsEmpty(List<String> fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return true; // 如果有空值返回 true
            }
        }
        return false; // 所有字段都非空返回 false
    }
}
