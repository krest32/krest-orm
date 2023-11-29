package com.krest.sorm.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInfo {
    /**
     * 字段的名称
     */
    private String name;

    /**
     * 字段的数据类型
     */
    private String dataType;
    /**
     * 字段的键类型（0：普通键 1 主键 2 外键）
     */
    private int keyType;
}

