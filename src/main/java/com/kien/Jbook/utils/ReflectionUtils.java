package com.kien.Jbook.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getPropertyValue(Object obj, String propertyName) {
        try {
            Field field = obj.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // ここに入るのは基本不可能、もしエラー発生したらグローバルハンドラに投げる
            throw new RuntimeException(e.getMessage());
        }
    }
}
