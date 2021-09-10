package com.jay;

import com.spring.context.SimpleApplicationContext;

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
        SimpleApplicationContext applicationContext = new SimpleApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("user"));
        System.out.println(applicationContext.getBean("user"));
        System.out.println(applicationContext.getBean("user"));

        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
    }
}
