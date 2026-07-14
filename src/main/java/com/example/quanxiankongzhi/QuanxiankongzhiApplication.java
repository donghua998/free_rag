package com.example.quanxiankongzhi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.quanxiankongzhi.**.mapper")
public class QuanxiankongzhiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanxiankongzhiApplication.class, args);
    }

}