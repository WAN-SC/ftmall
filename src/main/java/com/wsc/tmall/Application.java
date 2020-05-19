package com.wsc.tmall;

import com.wsc.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Application  extends SpringBootServletInitializer {
    static {
        PortUtil.checkPort(6379,"Redis ",true);
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
    @Override//
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
