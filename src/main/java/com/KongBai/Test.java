package com.KongBai;

import com.spring.KongBaiApplicationContext;

/**
 * @version 1.0
 * @Author qin
 * @Date 2022/11/29 20:50
 * 测试Bean工厂
 */
public class Test {
    public static void main(String[] args) {
        KongBaiApplicationContext kongBaiApplicationContext = new KongBaiApplicationContext(AppConfig.class);
        System.out.println(kongBaiApplicationContext.getBean("userService"));
        System.out.println(kongBaiApplicationContext.getBean("userService"));
        System.out.println(kongBaiApplicationContext.getBean("userService"));
    }
}
