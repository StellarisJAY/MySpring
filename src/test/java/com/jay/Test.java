package com.jay;

import com.jay.service.OrderService;
import com.jay.service.UserService;
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
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        UserService userService = (UserService) applicationContext.getBean("userService");

        System.out.println(orderService);
        userService.test();

        System.out.println(userService);
        orderService.test();

    }
}
