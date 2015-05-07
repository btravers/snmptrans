package com.zenika.snmptrans.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ServerJob {

    @Scheduled(fixedRate = 5000)
    public void execute() {

    }

}
