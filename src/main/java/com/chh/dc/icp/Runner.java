package com.chh.dc.icp;

import com.chh.dc.icp.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by Niow on 2016/6/27.
 */
@Configuration
@EnableAutoConfiguration
@ImportResource({"classpath:spring.xml"})
public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        log.info("采集服务平台启动加载");
        applicationContext = SpringApplication.run(Runner.class, args);
        log.info("Spring注入完毕");
        TaskManager taskManager = (TaskManager) applicationContext.getBean("taskManager", TaskManager.class);
        try {
            taskManager.start();
            log.info("采集服务平台启动完毕");
        } catch (Exception e) {
            log.info("采集服务平台启动失败", e);
        }
    }

    public static final <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

}
