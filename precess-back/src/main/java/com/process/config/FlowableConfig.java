package com.process.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 引擎配置
 */
@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        // 设置字体，解决流程图中文乱码问题
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");
        
        // 关闭定时任务 JOB，提高性能（如需定时任务，可以开启）
        engineConfiguration.setAsyncExecutorActivate(false);
        
        // 设置邮件服务器（如需发送邮件通知）
        // engineConfiguration.setMailServerHost("smtp.example.com");
        // engineConfiguration.setMailServerPort(25);
    }
}
