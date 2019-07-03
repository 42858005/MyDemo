package com.example.activiti;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan(basePackages="com.example.activiti.mapper")
// 扫描 所有需要的包, 包含一些自用的工具类包 所在的路径
public class ActivitiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiDemoApplication.class, args);
    }

}
