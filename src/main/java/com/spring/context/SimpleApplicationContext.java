package com.spring.context;

import com.spring.annotation.Component;
import com.spring.annotation.ComponentScan;

import java.io.File;
import java.net.URL;

/**
 * <p>
 * 最顶层基于注解和组件扫描的ApplicationContext
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class SimpleApplicationContext {
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
        }
    }

    public Object getBean(String beanName){
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
                            // 判断是否有 @Component注解
                            if(clazz.isAnnotationPresent(Component.class)){
                                // do register bean here
                                System.out.println("Component：" + clazz.getName());
                            }
                        } catch (ClassNotFoundException ignored) {

                        }
                    }
                }
            }
        }
    }

}