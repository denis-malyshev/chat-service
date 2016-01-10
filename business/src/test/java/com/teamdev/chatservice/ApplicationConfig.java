package com.teamdev.chatservice;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.teamdev.chat.service.impl","com.teamdev.chat.persistence"})
public class ApplicationConfig {

    private static final Logger LOG = Logger.getLogger(ApplicationConfig.class);

    @PostConstruct
    public void doSomething() {
        LOG.info("Spring was initialized.");
    }
}
