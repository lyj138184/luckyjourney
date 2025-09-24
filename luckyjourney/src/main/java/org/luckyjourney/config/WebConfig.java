package org.luckyjourney.config;

import org.luckyjourney.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @Author: Xhy
 * @CreateTime: 2023-10-24 16:27
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor(userService))
                .addPathPatterns("/admin/**","/authorize/**")
                .addPathPatterns("/luckyjourney/**")
                .excludePathPatterns("/luckyjourney/login/**","/luckyjourney/index/**","/luckyjourney/cdn/**", "/luckyjourney/file/**");

    }
}
