package com.krest.sorm.tools;

import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;

@Slf4j
public class JDBCUtils {
    /**
     * 执行参数化的Sql语句
     *
     * @param ps
     * @param params
     */
    public static Integer handleParams(PreparedStatement ps, Object[] params) {
        int count = 0;
        if (params != null) {
            try {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(1 + i, params[i]);
                    count++;
                }
                boolean execute = ps.execute();
                if (execute) {
                    log.info("Sql语句:【" + ps + "】  执行成功");
                } else {
                    throw new RuntimeException("执行错误");
                }
                return count;
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return 0;
    }
}
