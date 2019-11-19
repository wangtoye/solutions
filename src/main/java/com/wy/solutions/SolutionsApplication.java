package com.wy.solutions;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.wy.solutions.mapper")
@EnableTransactionManagement
@EnableSwagger2
public class SolutionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolutionsApplication.class, args);
    }

}
