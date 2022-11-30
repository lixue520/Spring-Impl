package com.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author qin
 * @Date 2022/11/29 20:51
 * 创建应用程序上下文，用于根据AppConfig用来解析它
 * A:扫描逻辑实现
 */
public class KongBaiApplicationContext {

    //配置类接收对象AppConfig
    private Class configClass;
    //单例池，确保根绝对象名实现单一对象的复用，这里主要用来实现Bean的作用域中的单例模式
    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();
    //存放所有定义的Bean
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public KongBaiApplicationContext(Class configClass){
        this.configClass = configClass;
        scan(configClass);

        for(Map.Entry<String,BeanDefinition> entry:beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if(beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanDefinition);  //单例Bean
                singletonObjects.put(beanName,bean);
            }

        }
    }

    //根据解析出来的Bean对象我们获取它的构造器来创建Bean并返回
    Object createBean(BeanDefinition beanDefinition){
        try {
            Object instance=beanDefinition.getClazz().getDeclaredConstructor().newInstance();
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void scan(Class configClass) {
        //A1.解析配置类
        //ComponentScan注解-->根据扫描路径去扫描-->扫描解析-->创建BeanDefinition-->放入BeanDefinitionMap
        //根据传进来的配置类AppConfig,并解析类名是否一致，打印出来.首先要获取注解对象，再获取该类注解中的注解参数
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value(); //扫描路径
        path = path.replace(".","/");
        System.out.println(path);

        //A2.扫描，将解析到的路径进行扫描获取该路径下的类，并利用类加载器来获取类的class对象，并利用JVM执行
        //类加载器有三种：
        /*
         * A.bootstrap: -->jre/lib
         * B.EXT: -->jre/lib/ext
         * C.App: -->classpath，这里我们主要用的就是它(应用类加载器)，获取资源(类资源\jar\txt等)
         */
        ClassLoader classLoader = KongBaiApplicationContext.class.getClassLoader();
        URL resource =classLoader.getResource(path);//可以是某个文件或者目录resource作为路径对象
        File file = new File(resource.getFile());
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File k: files) {
                System.out.println(k);
                /**
                 * 输出结果为:
                 * C:\Users\qin\Desktop\Spring-Impl\target\classes\com\KongBai\service\UserService.class
                 * C:\Users\qin\Desktop\Spring-Impl\target\classes\com\KongBai\service\XxUtil.class
                 */
                String fileName = k.getAbsolutePath(); //获取绝对路径
                if(fileName.endsWith(".class")){ //需要判断是否是.class文件再进行处理
                    //System.out.println(fileName);
                    //根据索引位截取字符串，从com开始拿到.class结束，不包含.class
                    String className  = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class"));
                    //System.out.println(className);
                    /**
                     * 输出结果为:
                     * com\KongBai\service\UserService
                     * com\KongBai\service\XxUtil
                     */

                    //替换字符串，将”/“替换成.这里由于"\"需要转义，所以需要写成"\\"
                    className = className.replace("\\",".");
                    System.out.println(className);

                    //A3.根据获取路后转义形成的点路径字符来调用类加载器
                    Class<?> clazz  = null;
                    try {
                        clazz = classLoader.loadClass(className);
                        if(clazz.isAnnotationPresent(Component.class)){
                            //A4.最终处理,根据AppConfig获得的路径，我们解析成了相对路径并形成.路径分割的形式，
                            //我们再将这种形式调用类加载器并获取得Class类对象clazz,再判断该clazz中是否有被运行时元注解
                            //注解的javaBean，因为被这样注解注解的才能最终进到这里。
                            //现在表示这里已经是一个Bean了，我们就可以根绝这个加载类的class的信息来创建这个类的Bean了，
                            //但是我们不能去直接创建它，因为我们需要考虑很多情况，所以我们需要创建Bean的作用域。
                            //并在代码进入到这里的时候去解析它到底是单例Bean(single)还是原型Bean(prototype)
                            //创建解析类-->BeanDefinition

                            Component componentAnnotation  = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            BeanDefinition beanDefinition = new BeanDefinition();
                            //如果存在Scope对象则作用域为原型
                            if(clazz.isAnnotationPresent(Scope.class)){
                              Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                              beanDefinition.setScope(scopeAnnotation.value());
                              beanDefinition.setClazz(clazz);
                            }else{
                                //如果不存在Scope对象则为单例
                                beanDefinition.setScope("singleton");
                                beanDefinition.setClazz(clazz);
                            }
                            //A5.将所有定义的bean放入到beanDefinitionMap中
                            beanDefinitionMap.put(beanName,beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }else {
                    System.out.println("当前文件不是.class文件，需要手工检查是否是class文件");
                }

            }
        }
    }

    public Object getBean(String beanName){
        if(beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton")){
                Object o = singletonObjects.get(beanName);
                return o;
            }else{
                //创建Bean对象,因为原型Bean每次都要生成不同的对象
                Object o = createBean(beanDefinition);
                return o;
            }
        }else {
            // 不存在对应的Bean
            throw new NullPointerException();
        }
    }
}
