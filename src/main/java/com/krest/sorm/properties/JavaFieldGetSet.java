package com.krest.sorm.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JavaFieldGetSet {
    /**
     * 属性源码信息
     */
    private String fieldInfo;

    /**
     * get源码信息，用来封装字段对应的get方法String
     */
    private String getInfo;

    /**
     * set方法源码信息，用来封装字段对应的Set方法String
     */
    private String setInfo;
}
