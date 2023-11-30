package com.krest.sorm.conn;

import com.krest.sorm.tools.Configuration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

@Data
@Slf4j
public class DBManager {

    private static Configuration conf;

    // 静态内部类，在调用程序的时候，遍建立连接
    static {
        Properties properties = new Properties();
        try {
            //通过相对路径读取文件
            InputStream in = new BufferedInputStream(new FileInputStream("src\\main\\resources\\application.properties"));
            properties.load(in);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        conf = new Configuration();
        //根据属性进行赋值
        String url = properties.getProperty("url");
        String[] split = url.split("/");
        String s = split[split.length - 1];
        String[] split1 = s.split("\\?");
        conf.setDBase(split1[0]);
        conf.setDriver(properties.getProperty("driver-class-name"));
        conf.setDbType(properties.getProperty("dbType"));
        conf.setUser(properties.getProperty("username"));
        conf.setPwd(properties.getProperty("password"));
        conf.setSrcPath(properties.getProperty("srcPath"));
        conf.setUrl(properties.getProperty("url"));
        conf.setTableName(properties.getProperty("tableName"));
        conf.setPoPackage(properties.getProperty("poPackage"));
        conf.setTableNameSign(properties.getProperty("tableNameSign"));
        conf.setColumnNameSign(properties.getProperty("columnNameSign"));
        conf.setQueryClass(properties.getProperty("queryClass"));
        conf.setPOOL_MAX_SIZE(Integer.parseInt(properties.getProperty("poolMaxSize")));
        conf.setPOOL_MIN_SIZE(Integer.parseInt(properties.getProperty("poolMinSize")));
    }

    private static DBConnPool connPool = new DBConnPool();

    // 从数据库连接池中获取链接对象
    public static Connection getCon() {
        return connPool.getConnection();
    }

    public static Connection createCon() {
        try {
            // 加载驱动对象
            Class.forName(conf.getDriver());
            // 建立链接对象
            Connection connection = DriverManager.getConnection(
                    conf.getUrl(),
                    conf.getUser(),
                    conf.getPwd()
            );
            return connection;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 关闭数据库链接方法
    public static void close(ResultSet rs, Statement ps, Connection conn) {
        try {
            if (rs != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            // 调用连接池的方法关闭链接
            connPool.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void close(Statement ps, Connection conn) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connPool.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 从配置文件中获取配置信息
    public static Configuration getConf() {
        return conf;
    }
}
