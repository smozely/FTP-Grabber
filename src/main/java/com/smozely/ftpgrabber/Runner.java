package com.smozely.ftpgrabber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan
@Configuration
@EnableScheduling
public class Runner {

    @Autowired
    private Synchronizer synchronizer;

    public static void main(String... args) {
        Runner thisRunner = SpringApplication.run(Runner.class, args).getBean(Runner.class);
        thisRunner.syncAndProcessFiles();

    }

    @Scheduled(cron = "0 0 * * * *")
    public void syncAndProcessFiles() {
        synchronizer.run();
        // TODO Process
    }


}
