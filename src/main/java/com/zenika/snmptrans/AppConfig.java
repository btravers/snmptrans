package com.zenika.snmptrans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ConnectionsConfig.class, SchedulerConfig.class })
@ComponentScan({"com.zenika.snmptrans.service",
        "com.zenika.snmptrans.repository",
        "com.zenika.snmptrans.utils"})
public class AppConfig {

    @Bean
    public static PropertyPlaceholderConfigurer configurer() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setIgnoreResourceNotFound(true);
        ppc.setSearchSystemEnvironment(true);
        ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return ppc;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
