package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
// 显式指定扫描 Controller 所在包
@ComponentScan(basePackages = { "com.example.backend", "com.example.backend.config", "com.example.backend.service",
        "com.example.backend.dao", "com.example.backend.controller" })
@ServletComponentScan("com.example.backend")

// @SpringBootApplication
public class PetCareApplication extends SpringBootServletInitializer {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.setDocumentRoot(new java.io.File("src/main/webapp"));
        };
    }

    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(2); // 设置优先级
        return resolver;
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // 允许从 src/main/webapp 加载所有资源
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:src/main/webapp/");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PetCareApplication.class, args);
        System.out.println("========================================");
        System.out.println("✅ 应用启动完成！");
        System.out.println("✅ 访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}