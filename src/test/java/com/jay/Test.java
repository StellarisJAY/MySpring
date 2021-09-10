package com.jay;

import com.jay.service.OrderService;
import com.spring.context.SimpleApplicationContext;

import java.lang.reflect.InvocationTargetException;

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

        System.out.println(applicationContext.getBean("userService"));

        ((OrderService)applicationContext.getBean("orderService")).test();
    }
}
