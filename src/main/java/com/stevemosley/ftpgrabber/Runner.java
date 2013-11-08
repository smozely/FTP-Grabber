package com.stevemosley.ftpgrabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Runner {

    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    public static void main(String... args) {
        LOG.info("About to start Spring Application Context.");
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/spring/ftpgrabber.xml");
    }

}
