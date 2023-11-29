package com.krest.sorm;

import java.lang.reflect.Method;
import java.sql.Timestamp;

public class ReflectUtils {
    public static Object invokeGet(String fileName, Object object) {
        //得到主键的Get方法
        Method method = null;
        try {
            Class<?> c = object.getClass();
            method = c.getDeclaredMethod("get" + fileName, null);
            Object keyValue = method.invoke(object, null);
            System.out.println(keyValue);
            return keyValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void invokeSet(Object object, String columnLabel, Object columnValue) {
        try {
            String fileName = StringUtils.firstChar2UpperCase(StringUtils.replaceStringSign(columnLabel));
            String string = columnValue.getClass().toString();
            // 时间格式的转换
            if (string.contains("Timestamp")) {
                Timestamp timestamp = (Timestamp) columnValue;
                java.util.Date tspToDate = new java.util.Date(timestamp.getTime());
                Method m = object.getClass().getDeclaredMethod(
                        "set" + fileName, tspToDate.getClass()
                );
                m.invoke(object, tspToDate);
                
            } else {
                Method m = object.getClass().getDeclaredMethod("set" + fileName,
                        columnValue.getClass());
                m.invoke(object, columnValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
