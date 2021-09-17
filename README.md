# MySpring
简单模拟spring框架的核心部分。

包括 IOC和简单的AOP

## 目前已完成功能

- 单例池，暂时没有实现原型bean和懒加载单例
- @ComponentScan组件扫描，目前只实现了这一种声明bean的方法
- bean的创建过程，构造方法推断、实例化、属性注入
- 解决循环依赖，只识别不解决构造方法注入的循环依赖，只解决属性注入的循环依赖
- AOP：以实现@Before前置处理

## 与原版Spring区别

1. @Autowired：不同于Spring采用的byType后再byName筛选，MySpring直接采用byName方式匹配。
2. AOP：MySpring的AOP只有在类实现了接口时才生效，且只是用JDK动态代理。不会向原版Spring一样使用Cglib来生成代理。
3. 目前只打算实现@ComponentScan扫描bean和@Bean两种bean声明方式。
4. AOP：简化了切入点表达式，可能无法支持复杂的业务场景



## 近期更新计划

1. 尽快完成AOP的@AfterReturn、@ThrowsException等切入点
2. 加入原型bean和懒加载单例
3. 加入@Bean的bean声明方式



## 长期更新计划

1. 实现Mybatis的对接
2. 使用netty编写http服务器，并在框架中整合MVC
3. 事件系统
4. 学习Springboot自动装配机制
