
package com.ian; // 建議放在 config 包下

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 允許所有 /api/ 路徑下的請求
                .allowedOrigins("http://localhost", "http://120.110.115.123","http://125.229.250.48:8081/") // 替換為您的前端實際運行網域或IP
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的 HTTP 方法
                .allowedHeaders("*") // 允許所有請求頭
                .allowCredentials(true) // 允許發送 Cookie 等憑證
                .maxAge(3600); // 預檢請求的緩存時間
    }
}