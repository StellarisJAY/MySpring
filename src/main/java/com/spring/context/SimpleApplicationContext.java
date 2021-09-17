package com.spring.context;

import com.spring.annotation.Component;
import com.spring.annotation.ComponentScan;
import com.spring.annotation.IsLazy;
import com.spring.annotation.Scope;
import com.spring.aop.annotation.AfterReturn;
import com.spring.aop.annotation.Aspect;
import com.spring.aop.annotation.Before;
import com.spring.aop.MethodWrapper;
import com.spring.aop.proxy.ProxyFactory;
import com.spring.bean.BeanDefinition;
import com.spring.bean.BeanPostProcessor;
import com.spring.bean.BeanRegistry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 最顶层基于注解和组件扫描的ApplicationContext
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class SimpleApplicationContext extends BeanRegistry {
    /**
     * 配置类
     */
    private Class<?> configClass;

    public SimpleApplicationContext(Class<?> configClass) {
        this.configClass = configClass;
        // 解析 ComponentScan注解，获得扫描包
        if(configClass.isAnnotationPresent(ComponentScan.class)){
            // 包名
            String basePackage = configClass.getDeclaredAnnotation(ComponentScan.class).basePackage();
            // 获取 App类加载器
            ClassLoader appClassLoader = SimpleApplicationContext.class.getClassLoader();
            // 组件扫描
            doComponentScan(basePackage, appClassLoader);

            createAspects();
            // 创建BeanDefinitionMap中的bean
            createBeans();

        }
    }

    public Object getBean(String beanName) {
        // 获取beanDefinition
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        // 没有beanDefinition记录，bean不存在
        if(beanDefinition == null){
            throw new RuntimeException("bean " + beanName + " doesnt exist");
        }
        // 单例非懒加载bean
        if(beanDefinition.getScope() == BeanDefinition.SINGLETON && !beanDefinition.isLazyInit()){
            return getSingleton(beanName);
        }
        // 懒加载单例bean
        else if(beanDefinition.isLazyInit()){
            // 创建bean
            return createBean(beanName,beanDefinition);
        }
        // 原型bean
        else if(beanDefinition.getScope() == BeanDefinition.PROTOTYPE){
            return createBean(beanName, beanDefinition);
        }
        return null;
    }

    public <T> T getBean(String beanName, Class<T> type){
        return null;
    }

    /**
     * 组件扫描方法
     * @param packageName 扫描包名
     * @param classLoader 使用的类加载器
     */
    private void doComponentScan(String packageName, ClassLoader classLoader){
        String path = packageName.replace(".", "/");
        URL resource = classLoader.getResource(path);
        File file;
        // 包路径存在
        if(resource != null && (file = new File(resource.getFile())).exists()){
            File[] files = file.listFiles();
            if(files != null){
                for(File f : files){
                    // 如果是目录，递归扫描子包
                    if(f.isDirectory()){
                        doComponentScan(packageName + "." + f.getName(), classLoader);
                    }
                    // 处理类文件
                    else if(f.getName().endsWith(".class")){
                        // 获取文件名
                        String filename = f.getName();
                        // 去除文件后缀，拼接 包名+类名
                        filename = filename.substring(0, filename.indexOf(".class"));
                        filename = packageName + "." + filename;
                        try {
                            // classLoader加载该类
                            Class<?> clazz = classLoader.loadClass(filename);
                            // 创建该bean的beanDefinition
                            BeanDefinition beanDefinition = new BeanDefinition(clazz);
                            List<Class<?>> interfaces = new ArrayList<>();
                            // 扫描beanClass 的接口
                            for(Class<?> impl : clazz.getInterfaces()){
                                if(impl == BeanPostProcessor.class){
                                    beanDefinition.setPostProcess(true);
                                }
                                else{
                                    interfaces.add(impl);
                                }
                            }
                            // 设置bean的接口
                            beanDefinition.setInterfaces(interfaces.toArray(new Class[0]));

                            // 判断是否有 @Component注解
                            if(clazz.isAnnotationPresent(Component.class)){
                                Component component = clazz.getDeclaredAnnotation(Component.class);
                                Scope scope = clazz.getDeclaredAnnotation(Scope.class);
                                String beanName = component.value();
                                // beanName 属性缺省，使用类名作为beanName
                                if(beanName.length() == 0){
                                    beanName = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);
                                }

                                // 单例 bean
                                if(scope == null || "singleton".equals(scope.value().toLowerCase())){
                                    beanDefinition.setScope(BeanDefinition.SINGLETON);
                                    beanDefinition.setLazyInit(clazz.isAnnotationPresent(IsLazy.class));
                                }
                                // 原型 bean
                                else if("prototype".equals(scope.value().toLowerCase())){
                                    beanDefinition.setScope(BeanDefinition.PROTOTYPE);
                                }
                                // 无效的scope
                                else{
                                    throw new RuntimeException("invalid scope " + scope.value() + " for bean " + beanName);
                                }

                                // 设置切面类
                                if(clazz.isAnnotationPresent(Aspect.class)){
                                    beanDefinition.setLazyInit(false);
                                    beanDefinition.setScope(BeanDefinition.SINGLETON);
                                    beanDefinition.setAspect(true);
                                }else{
                                    beanDefinition.setAspect(false);
                                }
                                // 记录 beanDefinition
                                addBeanDefinition(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException ignored) {

                        }
                    }
                }
            }
        }
    }

    /**
     * ComponentScan阶段的bean创建
     */
    private void createBeans() {
        // 获取beanDefinition
        ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = super.beanDefinitionMap;

        for(String beanName : beanDefinitionMap.keySet()){
            System.out.println("creating bean : " + beanName);
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope() == BeanDefinition.SINGLETON && !beanDefinition.isLazyInit() && !beanDefinition.isAspect()){
                Object instance = createBean(beanName, beanDefinition);
            }
        }
    }

    /**
     * 创建切面对象
     */
    private void createAspects(){
        ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = super.beanDefinitionMap;
        for(String beanName : beanDefinitionMap.keySet()){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            // 判断bean是否是切面
            if(beanDefinition.isAspect()){
                // 创建bean
                Object instance = createBean(beanName, beanDefinition);
                Class<?> beanClass = beanDefinition.getBeanClass();
                Method[] methods = beanClass.getDeclaredMethods();
                // 对通知方法进行封装和记录
                for(Method method : methods){
                    MethodWrapper methodWrapper = null;
                    // before
                    if(method.isAnnotationPresent(Before.class)){
                        Before before = method.getAnnotation(Before.class);
                        if(before.value().length() == 0){
                            throw new RuntimeException("@Before注解必须带有value属性");
                        }
                        methodWrapper= new MethodWrapper(before.value(), instance, method);
                        proxyFactory.registerMethodFilter(methodWrapper, ProxyFactory.MethodFilterType.BEFORE);
                    }
                    if(method.isAnnotationPresent(AfterReturn.class)){
                        AfterReturn afterReturn = method.getAnnotation(AfterReturn.class);
                        if(afterReturn.value().length() == 0){
                            throw new RuntimeException("@AfterReturn 必须有value属性");
                        }
                        methodWrapper = new MethodWrapper(afterReturn.value(), instance, method);
                        proxyFactory.registerMethodFilter(methodWrapper, ProxyFactory.MethodFilterType.AFTER_RETURN);
                    }
                }
            }
        }
    }

}
