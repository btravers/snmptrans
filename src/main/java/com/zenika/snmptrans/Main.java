package com.zenika.snmptrans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        ApplicationContext applicationContext = SpringApplication.run(AppConfig.class, args);

    }

}
