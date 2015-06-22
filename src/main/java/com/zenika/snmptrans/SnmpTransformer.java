package com.zenika.snmptrans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SnmpTransformer {
    private static final Logger logger = LoggerFactory.getLogger(SnmpTransformer.class);

    public static void main(String... args) {
        logger.info("Starting snmptrans.");
        ApplicationContext applicationContext = SpringApplication.run(AppConfig.class);
    }

}
