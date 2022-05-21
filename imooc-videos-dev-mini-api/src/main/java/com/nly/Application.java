package com.nly;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableAutoConfiguration(exclude={DruidDataSourceAutoConfigure.class})
@MapperScan("com.nly.mapper")//用于扫描mapper接口
@ComponentScan(basePackages= {"com.nly", "org.n3r.idworker"})
public class Application {

    public static void main(String[] args) {

       /*
       //遍历启动时候加载的类
       ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        for (String name : applicationContext.getBeanDefinitionNames()) {
            System.out.println(name);
        }*/

       SpringApplication.run(Application.class,args);

    }
}
