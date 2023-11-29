package com.krest.sorm.query;

import com.krest.sorm.properties.ColumnInfo;
import com.krest.sorm.properties.TableInfo;
import com.krest.sorm.properties.TypeConvertorImpl;
import com.krest.sorm.properties.TypeConvevrtor;
import com.krest.sorm.tools.Configuration;
import com.krest.sorm.conn.DBManager;
import com.krest.sorm.tools.JavaFileUtils;
import com.krest.sorm.tools.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableContext {
    /**
     * 表名为Key，表信息为value,可以封装多张数据表的信息
     */
    private static Map<String, TableInfo> tables = new HashMap<String, TableInfo>();
    /**
     * 将实体类和表信息关联起来，放入Map中便于重新使用
     */
    protected static Map<Class, TableInfo> poClassTableMap = new HashMap<Class, TableInfo>();

    /**
     * 无参构造方法
     */
    private TableContext() {
    }

    /**
     * 在调用TableContext的相关方法是便会执行
     */
    static {
        try {
            // 获取配置信息
            Configuration configuration = DBManager.getConf();
            // 初始化获得链接对象--> 这个过程会消耗很多的资源
            Connection con = DBManager.getCon();
            // 通过链接，得到数据库的元信息
            DatabaseMetaData dbmd = con.getMetaData();
            /**
             * 从url中，获取数据库名称
             */
            String url = dbmd.getURL();
            String[] split = url.split("/");
            String s = split[split.length - 1];
            String[] split1 = s.split("\\?");
            String dBase = split1[0];

            // 获取数据库的基本元信息
            System.out.println("数据库已知的用户: " + dbmd.getUserName());
            System.out.println("数据库的系统函数的逗号分隔列表: " + dbmd.getSystemFunctions());
            System.out.println("数据库的时间和日期函数的逗号分隔列表: " + dbmd.getTimeDateFunctions());
            System.out.println("数据库的字符串函数的逗号分隔列表: " + dbmd.getStringFunctions());
            System.out.println("数据库供应商用于 'schema' 的首选术语: " + dbmd.getSchemaTerm());
            System.out.println("数据库URL: " + dbmd.getURL());
            System.out.println("是否允许只读:" + dbmd.isReadOnly());
            System.out.println("数据库的产品名称:" + dbmd.getDatabaseProductName());
            System.out.println("数据库的版本:" + dbmd.getDatabaseProductVersion());
            System.out.println("驱动程序的名称:" + dbmd.getDriverName());
            System.out.println("驱动程序的版本:" + dbmd.getDriverVersion());

            String[] str = {"TABLE"};
            // 显示能够得到的数据库中所有的表格信息
            ResultSet tables1 = dbmd.getTables(null, null, "%", str);
            while (tables1.next()) {
                //表名
                String tableName = tables1.getString("TABLE_NAME");
                //表类型
                String tableType = tables1.getString("TABLE_TYPE");
                //表备注
                String remarks = tables1.getString("REMARKS");
                //显示表信息
                System.out.println(tableName + " - " + tableType + " - " + remarks);
            }


            ResultSet tableRet = null;
            // 判断配置信息中是否有设置单张表的信息，如果没有那就获得数据库中所有的数据表的信息
            if (!StringUtils.isEmpty(configuration.getTableName())) {
                tableRet = dbmd.getTables(dBase, "%", configuration.getTableName(), str);
            } else {
                tableRet = dbmd.getTables(null, null, "%", str);
            }

            // 开始遍历得到符合条件的表的信息
            while (tableRet.next()) {
                String tableName = (String) tableRet.getObject("TABLE_NAME");
                String tableType = tableRet.getString("TABLE_TYPE");

                System.out.println("表名： " + tableName + " - 类型： " + tableType);
                TableInfo tableInfo = new TableInfo(tableName, new ArrayList<ColumnInfo>(), new HashMap<String, ColumnInfo>());
                tableInfo.setTname(tableName);
                tables.put(tableName, tableInfo);

                // 查询表中所有字段
                ResultSet set = dbmd.getColumns(null, "%", tableName, "%");
                System.out.println(set.toString());
                Map<String, ColumnInfo> map = new HashMap<>();

                // 遍历表头字段信息
                while (set.next()) {
                    String columnName = set.getString("COLUMN_NAME");
                    String columnType = set.getString("TYPE_NAME");
                    int datasize = set.getInt("COLUMN_SIZE");
                    int digits = set.getInt("DECIMAL_DIGITS");
                    int nullable = set.getInt("NULLABLE");
                    System.out.println(columnName + " " + columnType + " " + datasize + " " + digits + " " + nullable);

                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setName(columnName);
                    columnInfo.setDataType(columnType);
                    columnInfo.setKeyType(0);

                    // 将单张表的所有字段存储到Map集合中
                    map.put(columnName, columnInfo);
                    tableInfo.setColumns(map);
                }

                List<ColumnInfo> list = new ArrayList<>();
                ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
                while (set2.next()) {
                    Map<String, ColumnInfo> columns = tableInfo.getColumns();
                    String column_name = (String) set2.getObject("COLUMN_NAME");
                    System.out.println("column_name:" + column_name);
                    ColumnInfo columnInfo2 = columns.get(column_name);
                    // 设置位主键类型
                    columnInfo2.setKeyType(1);
                    list.add(columnInfo2);
                }
                tableInfo.setPriKeys(list);
                System.out.println("tableInfo.PriKeys" + tableInfo.getPriKeys());


                // 添加主键信息，只针对表中有一个主键，不支持外键
                if (tableInfo.getPriKeys().size() > 0) {
                    tableInfo.setOnlyKey(tableInfo.getPriKeys().get(0));
                }
            }

            if (tables.isEmpty()) {
                throw new RuntimeException("没有找到数据库信息，请检查配置信息");
            }

            // 根据得到的Table信息对应的生成Java实体类文件
            createJavaFile();

            //加载类名与表的对应关系
            loadPdTables();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, TableInfo> getTableInfos() {
        return tables;
    }

    /**
     * 加载实体类，如果还没有生成entity实体类，那么第一次加载会报错，第二次以后就可以正常加载了
     */
    public static void loadPdTables() {
        // 遍历所有的Tables中的信息
        for (TableInfo tableInfo : tables.values()) {
            try {
                // 获得Tables中所有对应实体类的名称
                String className = DBManager.getConf().getPoPackage() + ".entity." +
                        StringUtils.firstChar2UpperCase(
                                StringUtils.replaceStringSign(tableInfo.getTname()));
                className.replaceAll(" ", "");

                // 通过反射获取对象
                Class<?> aClass = Class.forName(className);
                poClassTableMap.put(aClass, tableInfo);

                // 测试代码
                for (TableInfo value : poClassTableMap.values()) {
                    System.out.println("poClassTableMap:" + value);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建与数据表信息对应的实体类文件
     */
    public static void createJavaFile() {
        // 表字段类型装换器
        TypeConvevrtor typeConvevrtor = new TypeConvertorImpl();
        Map<String, TableInfo> tables = getTableInfos();
        // 循环生成Java实体类文件
        for (TableInfo value : tables.values()) {
            JavaFileUtils.createJavaPOFile(value, typeConvevrtor);
        }
    }
}
