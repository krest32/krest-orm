package com.krest.sorm.properties;

public interface TypeConvevrtor {
    /**
     * 将数据库的字段类型转化为Java数据类型
     * @param columnType
     * @return 返回java数据类型
     */
    public  String databaseType2JavaType(String columnType);

    /**
     * 将java字段类型转化为数据库类型
     * @param javaType
     * @return 返回java数据类型
     */
    public  String javaType2DatabaseType(String javaType);
}
