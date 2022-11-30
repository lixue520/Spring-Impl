package com.KongBai;

import com.spring.ComponentScan;

/**
 * @version 1.0
 * @Author qin
 * @Date 2022/11/29 20:49
 * 用于开启事务和Aop服务等，用于配置类
 */
@ComponentScan("com.KongBai.service")
public class AppConfig {
    //定义@bean,事务等，还可以后续添加配置
}
