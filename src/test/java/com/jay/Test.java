package com.jay;

import com.spring.SimpleApplicationContext;

/**
 * <p>
 * 测试类
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class Test {
    public static void main(String[] args) {
        SimpleApplicationContext applicationContext = new SimpleApplicationContext(BeanConfig.class);
    }
}
