package com.smozely.ftpgrabber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan
@Configuration
public class Runner {

    public static void main(String... args) {
        ConfigurableApplicationContext context = SpringApplication.run(Runner.class, args);
        FtpSync ftpSync = context.getBean(FtpSync.class);
        ftpSync.run();
    }

}
