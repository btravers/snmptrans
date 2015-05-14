package com.zenika.snmptrans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ConnectionsConfig.class, UtilsConfig.class, SchedulerConfig.class })
@ComponentScan({"com.zenika.snmptrans.service", "com.zenika.snmptrans.repository"})
public class AppConfig {

    @Value("${continue.on.json.error:false}")
    private boolean continueOnJsonError;

    @Value("${run.endlessly:false}")
    private boolean runEndlessly;



}
