package com.krest;


import com.krest.sorm.MySqlQuery;
import com.krest.sorm.Query;
import com.krest.sorm.QueryFactory;
import org.junit.Test;

import java.util.List;

public class Demo {

    @Test
    public void test() throws Exception {
        MySqlQuery mySqlQuery = new MySqlQuery();
        // 测试 queryRows
        List<Sites> list = mySqlQuery.queryRows(
                "select * from demo_ali_account where money>? and money<?;",
                Sites.class, new Object[]{1, 6000});
        for (Sites o : list) {
            System.out.println(o.getName() + ":" + o.getUrl());
        }

        //  测试 queryValue
        Object o = mySqlQuery.queryValue("select money from demo_ali_account where count_id=?;",
                new Object[]{"1"});
        System.out.println(o);

        //  测试数据库连接池的效率
        Long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            QueryTest();
        }
        Long end = System.currentTimeMillis();
        // 结果不加连接池花费32s, 加入连接池花费4s；
        System.out.println(end - start);


    }

    @Test
    public void QueryTest() throws CloneNotSupportedException {
        Query query = QueryFactory.createQuery();
        List<Sites> list = query.queryRows(
                "select * from sites where name=? or name=?;",
                Sites.class,
                new Object[]{"RUNOOB", "Google"}
        );
        for (Sites o : list) {
            System.out.println(o.getName() + ":" + o.getUrl());
        }
    }
}

