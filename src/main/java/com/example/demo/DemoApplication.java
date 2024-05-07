package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Handler;

@SpringBootApplication
public class DemoApplication {



        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
        }

        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(apiLoggingInterceptor());
                    registry.addInterceptor(legacyInterceptor());
                }
            };
        }

        @Bean
        public HandlerInterceptor apiLoggingInterceptor() {
            return new HandlerInterceptor() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    System.out.println("User-Agent: " + request.getHeader("User-Agent"));
                    return true;
                }
            };
        }

        @Bean
        public HandlerInterceptor legacyInterceptor() {
            return new HandlerInterceptor() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
                    // Assuming legacy endpoint is "/legacy"
                    if (request.getRequestURI().equals("/legacy")) {
                        response.sendError(HttpServletResponse.SC_GONE);
                        return false;
                    }
                    return true;
                }
            };
        }
    }

    @RestController
    class BasicController {
        @GetMapping("/time")
        public LocalDateTime getTime() {
            return LocalDateTime.now();
        }
    }

    @RestController
    class LegacyController {
        @GetMapping("/legacy")
        public String getLegacyContent() {
            return "This is just old code";
        }
    }
