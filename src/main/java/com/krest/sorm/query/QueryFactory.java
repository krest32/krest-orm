package com.krest.sorm.query;

import com.krest.sorm.conn.DBManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryFactory {

    private static QueryFactory queryFactory = new QueryFactory();
    /**
     * 创建原型对象
     */
    private static Query protoTypeObj;

    static {
        // 加载指定 Query 类
        try {
            Class aClass = Class.forName(DBManager.getConf().getQueryClass());
            protoTypeObj = (Query) aClass.newInstance();
            log.info("加载Query对象文件");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    //私有构造器
    private QueryFactory() {
    }

    public static Query createQuery() throws CloneNotSupportedException {
        return (Query) protoTypeObj.clone();
    }
}
