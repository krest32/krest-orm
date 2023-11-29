package com.krest.sorm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {

    /**
     * 数据库链接Driver
     */
    private String driver;

    /**
     * 数据库链接url
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String user;


    /**
     * 数据库链接密码
     */
    private String pwd;


    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 数据库pojo文件生成地址
     */
    private String srcPath;

    /**
     * 数据库名称
     */
    private String dBase;


    /**
     * 数据表名称
     */
    private String tableName;


    /**
     * 数据库pojo文件生成的包名
     */
    private String poPackage;

    /**
     * 表名之间的间隔符号
     */
    private String tableNameSign;

    /**
     * 列名之间的间隔符号
     */
    private String columnNameSign;

    /**
     * 列名之间的间隔符号
     */
    private String queryClass;

    /**
     *     最大连接数
     */
    private int POOL_MAX_SIZE =100;

    /**
     *     最小连接数
     */
    private int POOL_MIN_SIZE =10;
}
