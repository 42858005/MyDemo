package com.example.activiti.utils;

import tk.mybatis.mapper.entity.Example;

public class MyExample {

    public static Object happy(Object o) throws ClassNotFoundException {
        String className = o.getClass().getName();

        Example example = new Example(Class.forName(className));
        Example.Criteria criteria = example.createCriteria();
        return null;

    }
}
