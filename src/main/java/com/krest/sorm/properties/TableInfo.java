package com.krest.sorm.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    /**
     * 表名
     */
    private String tname;

    /**
     * 所有字段信息
     */
    private Map<String, ColumnInfo> columns;

    /**
     * 如果有联合主键，那么就在这里存储
     */
    private List<ColumnInfo> priKeys;

    /**
     * 唯一主键信息
     */
    private ColumnInfo onlyKey;

    // 有参构造方法——> 没有添加Key的信息
    public TableInfo(String tableName, ArrayList<ColumnInfo> columnInfos,
                     HashMap<String, ColumnInfo> stringColumnInfoHashMap) {
    }
}

