package com.krest.sorm;

public class StringUtils {

    /**
     * 将首字母大写
     *
     * @return
     */
    public static String firstChar2UpperCase(String str) {
        // abc-> Abc
        return str.toUpperCase().substring(0, 1) + str.substring(1);
    }

    // 将固定的字段格式化
    public static String replaceStringSign(String name) {
        String columnNameSign = DBManager.getConf().getColumnNameSign();
        String tableNameSign = DBManager.getConf().getTableNameSign();
        String sign;
        String str = null;
        if (!org.springframework.util.StringUtils.isEmpty(columnNameSign) &&
                !org.springframework.util.StringUtils.isEmpty(tableNameSign)) {
            if (columnNameSign.equals(tableNameSign)) {
                sign = columnNameSign;
                str = getString(name, sign);
            } else {
                if (name.contains(columnNameSign) && name.contains(tableNameSign)) {
                    throw new RuntimeException("数据库或表命名不规范！！！");
                }
                if (name.contains(columnNameSign)) {
                    sign = columnNameSign;
                    str = getString(name, sign);
                }
                if (name.contains(tableNameSign)) {
                    sign = tableNameSign;
                    str = getString(name, sign);
                }
            }
        } else {
            if (!org.springframework.util.StringUtils.isEmpty(columnNameSign)) {
                sign = columnNameSign;
                str = getString(name, sign);
            }
            if (!org.springframework.util.StringUtils.isEmpty(tableNameSign)) {
                sign = tableNameSign;
                str = getString(name, sign);
            }
        }
        return str;
    }

    //去掉表中所有的个字段符号
    private static String getString(String name, String sign) {
        StringBuilder sName = new StringBuilder();
        // 切换大小写
        if (name.contains(sign)) {
            String[] s = name.split(sign);
            for (int i = 0; i < s.length; i++) {
                if (i == 0) {
                    sName.append(s[i]);
                } else {
                    char ch = s[i].charAt(0);
                    char t = Character.toUpperCase(ch);
                    String rest = s[i].substring(1);
                    s[i] = t + rest;
                    sName.append(s[i]);
                }
            }
        } else {
            sName.append(name);
        }
        return sName.toString();
    }

    static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }
}

