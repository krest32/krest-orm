package com.krest.sorm.query;

import com.krest.sorm.conn.DBManager;
import com.krest.sorm.properties.ColumnInfo;
import com.krest.sorm.properties.TableInfo;
import com.krest.sorm.tools.JDBCUtils;
import com.krest.sorm.tools.ReflectUtils;
import com.krest.sorm.tools.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class Query implements Cloneable, Serializable {

    //复制对象的Clone方法
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 执行查询的模版方法
     *
     * @param sql
     * @param params
     * @param clazz
     * @param callBack
     * @return
     */
    public Object executeQueryTemplate(String sql, Object[] params, Class clazz, CallBack callBack) {
        Connection con = DBManager.getCon();
        // 用来存放查询的结果
        List list = null;
        PreparedStatement ps = null;
        Integer integer = 0;
        ResultSet resultSet = null;
        try {
            ps = con.prepareStatement(sql);
            // 构建需要执行的 Sql 语句
            JDBCUtils.handleParams(ps, params);
            resultSet = ps.executeQuery();
            return callBack.doExcute(con, ps, resultSet);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DBManager.close(ps, con);
        }
    }

    /**
     * 帮助我们直接执行一个DML语句
     *
     * @param Sql
     * @param params
     * @return 执行Sql影响的数据库行数
     */
    public int executeDML(String Sql, Object[] params) {
        Connection con = DBManager.getCon();
        PreparedStatement ps = null;
        Integer integer = 0;
        try {
            ps = con.prepareStatement(Sql);
            integer = JDBCUtils.handleParams(ps, params);
            System.out.println(ps);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(ps, con);
        }
        return integer;
    }

    ;

    /**
     * 将数据存储到数据库中
     * 将对象中的null元素不进行存储
     *
     * @param object
     */

    public void insert(Object object) {
        //通过反射获取基本的数据信息
        Class<?> aClass = object.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(aClass);
        Map<String, ColumnInfo> columns = tableInfo.getColumns();

        // 开始拼接Sql语句
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + tableInfo.getTname() + " (");

        // 得到插入的数据信息
        List<Object> params = new ArrayList<>();

        Field[] fs = aClass.getDeclaredFields();
        // 计算不为空的属性值
        int notNullField = 0;

        for (Field f : fs) {
            String fName = StringUtils.firstChar2UpperCase(f.getName());
            Object fValue = ReflectUtils.invokeGet(fName, object);
            if (!org.springframework.util.StringUtils.isEmpty(fValue)) {

                notNullField++;
                for (ColumnInfo value : columns.values()) {
                    String columnsName = StringUtils.replaceStringSign(value.getName());
                    if (fName.equalsIgnoreCase(columnsName)) {
                        sql.append(value.getName() + ",");
                        params.add(fValue);
                    }
                }
            }
        }
        // 替换指定位置的字符
        sql.setCharAt(sql.length() - 1, ')');

        sql.append(" values ( ");
        for (int i = 0; i < notNullField; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length() - 1, ')');
        System.out.println(sql);

        executeDML(sql.toString(), params.toArray());
    }

    /**
     * 根据Id删除数据信息
     *
     * @param clazz
     * @param id
     */
    public void delete(Class clazz, Object id) {
        //通过Class对象绑定tableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        // 获得主键信息
        ColumnInfo onlyKey = tableInfo.getOnlyKey();
        // 编写Sql语句的执行String
        String sql = "delete from " + tableInfo.getTname() + " where " + onlyKey.getName() + "=? ";
        // 执行Sql
        int i = executeDML(sql, new Object[]{id});
        System.out.println("删除了" + i + "条数据");

    }

    /**
     * 根据对象，删除信息
     *
     * @param object
     * @return
     * @throws Exception
     */
    public Integer delete(Object object) throws Exception {
        // 通过反射获取Table信息
        TableInfo tableInfo = TableContext.poClassTableMap.get(object.getClass());
        // 获得主键信息
        ColumnInfo onlyKey = tableInfo.getOnlyKey();
        //得到反射调用Get方法得到d
        String fileName = StringUtils.firstChar2UpperCase(StringUtils.replaceStringSign(onlyKey.getName()));
        Object keyValue = ReflectUtils.invokeGet(fileName, object);
        //编写Sql语句的执行String
        String sql = "delete from " + tableInfo.getTname() + " where " + onlyKey.getName() + "=? ";
        //执行Sql
        int i = executeDML(sql, new Object[]{keyValue});
        System.out.println("删除了" + i + "条数据");
        return i;
    }

    /**
     * update 方法会比较复杂一些
     * 如: mySqlQuery.update(aliAccount,new String[]{"name","peopleId","money"});
     *
     * @param object
     * @param fieldNames
     * @return
     */
    public int update(Object object, String[] fieldNames) {
        // 更新语句模版: UPDATE table_name SET field1=new-value1, field2=new-value2 where id=?
        Class<?> aClass = object.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(aClass);
        List<Object> params = new ArrayList<>();
        Map<String, ColumnInfo> columns = tableInfo.getColumns();
        //获得唯一主键信息
        ColumnInfo onlyKey = tableInfo.getOnlyKey();
        // 获取主键的id值
        String s = StringUtils.firstChar2UpperCase(StringUtils.replaceStringSign(onlyKey.getName()));
        Object id = ReflectUtils.invokeGet(s, object);

        System.out.println("tableInfo:" + tableInfo.toString());

        // 开始拼接Sql语句
        StringBuilder sql = new StringBuilder();
        sql.append("update " + tableInfo.getTname() + " set ");

        for (String fieldName : fieldNames) {
            String fName = StringUtils.firstChar2UpperCase(fieldName);
            Object fValue = ReflectUtils.invokeGet(fName, object);
            // 验证
            for (ColumnInfo value : columns.values()) {
                String columnsName = StringUtils.replaceStringSign(value.getName());
                if (fName.equalsIgnoreCase(columnsName)) {
                    sql.append(value.getName() + "=?,");
                    params.add(fValue);
                }
            }
        }

        sql.setCharAt(sql.length() - 1, ' ');
        sql.append(" where ");
        sql.append(onlyKey.getName() + "=" + id);

        System.out.println(sql);
        return executeDML(sql.toString(), params.toArray());
    }

    public List queryRows(final String sql, final Class clazz, final Object[] params) {
        // 用来存放查询的结果
        return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {
            @Override
            public Object doExcute(Connection con, PreparedStatement ps, ResultSet rs) {
                List list = null;
                try {
                    ResultSetMetaData metaData = rs.getMetaData();
                    // 处理返回的多行数据
                    while (rs.next()) {
                        if (list == null) {
                            list = new ArrayList();
                        }
                        // 调用 entity 实体类的构造函数生成对象
                        Object rowObj = clazz.newInstance();
                        // 处理多行数据中的多列数据
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            // 得到返回值
                            String columnLabel = metaData.getColumnLabel(i + 1);
                            Object columnValue = rs.getObject(i + 1);

                            if (!Objects.isNull(columnValue)) {
                                log.info("columnLabel:" + columnLabel);
                                log.info("columnValue:" + columnValue);
                                //通过调用set方法，设置对象的值
                                ReflectUtils.invokeSet(rowObj, columnLabel, columnValue);
                            }
                        }
                        list.add(rowObj);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                return list;
            }
        });
    }

    public Object queryUniqueRow(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql, clazz, params);
        return (list == null && list.size() > 0) ? null : list.get(0);
    }


    public Object queryValue(String sql, Object[] params) {

        // 利用方法模版进行回调，减少重复代码
        return executeQueryTemplate(sql, params, null, new CallBack() {

            @Override
            public Object doExcute(Connection con, PreparedStatement ps, ResultSet rs) {
                Object value = null;
                try {
                    while (rs.next()) {
                        value = rs.getObject(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });

    }


    public Number queryNumber(String sql, Object[] params) {
        return (Number) queryValue(sql, params);
    }

    /**
     * 分页查询方法
     *
     * @param page  第几页
     * @param limit 每页显示几项
     * @return 分页查询内容
     */
    public abstract Object pageQery(Long page, Long limit);
}
