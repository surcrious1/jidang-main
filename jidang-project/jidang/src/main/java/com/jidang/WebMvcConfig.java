package com.jidang;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // uploadPath 예: C:/project/uploads or /home/ubuntu/uploads
        String normalizedPath =
                uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";

        registry.addResourceHandler("/uploads/**")       // URL로 접근할 경로
                .addResourceLocations("file:" + normalizedPath);  // 실제 파일 위치
    }
}
