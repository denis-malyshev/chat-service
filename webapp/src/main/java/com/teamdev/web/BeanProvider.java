package com.teamdev.web;

import com.teamdev.chat.service.impl.application.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanProvider {

    private static BeanProvider ourInstance = new BeanProvider();

    private ApplicationContext context;

    public static BeanProvider getInstance() {
        return ourInstance;
    }

    private BeanProvider() {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    }

    public <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
