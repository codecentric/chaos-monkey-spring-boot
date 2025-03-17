package com.example.chaos.monkey.chaosdemo.bean;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {
    @Bean
    public FilterRegistrationBean<CrossSiteScriptingFilter> crossSiteScriptingFilterBean(){
        FilterRegistrationBean<CrossSiteScriptingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CrossSiteScriptingFilter());
        registrationBean.setOrder(0);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }


}
