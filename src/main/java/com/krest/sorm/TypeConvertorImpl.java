package com.krest.sorm;

public class TypeConvertorImpl implements TypeConvevrtor {
    @Override
    public String databaseType2JavaType(String columnType) {
        if ("varchar".equalsIgnoreCase(columnType)||
                "char".equalsIgnoreCase(columnType)||
                "LONGTEXT".equalsIgnoreCase(columnType)||
                "TEXT".equalsIgnoreCase(columnType)){
            return "String";
        }else if ("int".equalsIgnoreCase(columnType)||
                "BIT".equalsIgnoreCase(columnType)||
                "INT UNSIGNED".equalsIgnoreCase(columnType)||
                "tinyint".equalsIgnoreCase(columnType)||
                "tinyint UNSIGNED".equalsIgnoreCase(columnType)||
                "smallint".equalsIgnoreCase(columnType)||
                "integer".equalsIgnoreCase(columnType)){
            return "Integer";
        }else if("bigint".equalsIgnoreCase(columnType)||
                "BIGINT UNSIGNED".equalsIgnoreCase(columnType)){
            return "Long";
        }else if("float".equalsIgnoreCase(columnType)||
                "double".equalsIgnoreCase(columnType)){
            return "DOuble";
        }else if("clob".equalsIgnoreCase(columnType)){
            return "java.sql.CLob";
        }else if("blob".equalsIgnoreCase(columnType)||
                "LONGBLOB".equalsIgnoreCase(columnType)) {
            return "java.sql.Blob";
        }else if("date".equalsIgnoreCase(columnType)||"datetime".equalsIgnoreCase(columnType)){
            return "java.util.Date";
        }else if("time".equalsIgnoreCase(columnType)){
            return "java.sql.time";
        }else if("Timestamp".equalsIgnoreCase(columnType)){
            return "java.sql.Timestamp";
        }else {
            throw new RuntimeException(columnType+"没有该数据类型，请添加");
        }
    }
    @Override
    public String javaType2DatabaseType(String javaType) {
        return null;
    }
}

