package com.krest.sorm.conn;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DBConnPool {
    /**
     * 连接对象的集合
     */
    private static List<Connection> pool;
    /**
     * 最大连接数，在配置文件中进行配置，使用final进行修饰，所以配置文件中一定要配置这个数据
     */
    private static final int POOL_MAX_SIZE = DBManager.getConf().getPOOL_MAX_SIZE();
    /**
     * 最小连接数，在配置文件中进行配置，使用final进行修饰，所以配置文件中一定要配置这个数据
     */
    private static final int POOL_MIN_SIZE = DBManager.getConf().getPOOL_MIN_SIZE();

    // 初始化连接池
    public void initPool() {
        if (pool == null) {
            pool = new ArrayList<Connection>();
        }
        while (pool.size() < DBConnPool.POOL_MIN_SIZE) {
            Connection con = DBManager.createCon();
            pool.add(con);
        }
    }

    /**
     * 通过无参数的构造方法，在调用初期就默认创建好连接池。
     */
    public DBConnPool() {
        initPool();
    }

    /**
     * 从pool中获取一个链接对象，同时加上同步锁，避免多线程的情况
     */
    public synchronized Connection getConnection() {
        int lastIndex = pool.size() - 1;
        // 当使用使用对象的个数超过容器存储的数量是，遍执行新建功能
        if (lastIndex == 0) {
            // 如果链接池中没有对象，那么就新建一个链接对象进行返回
            return DBManager.createCon();
        } else {
            Connection connection = pool.get(lastIndex);
            // 当获取该连接后，从池中删除，避免被其他对象再次调用
            pool.remove(lastIndex);
            return connection;
        }
    }

    /**
     * 将连接放回pool中，达到重读利用的目的
     *
     * @param connection
     */
    public synchronized void close(Connection connection) {
        // 如果链接池中的对象数量超过了最大值，执行关闭操作
        if (pool.size() > POOL_MAX_SIZE) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pool.add(connection);
        }
    }
}
