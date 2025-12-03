package com.perfectdigitalsociety.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * CORS configuration for web MVC - Disabled, using SecurityConfig instead
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS is handled in SecurityConfig to avoid conflicts
        // registry.addMapping("/**")...
    }
    
    /**
     * Resource handler configuration
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // Profile pictures and uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
    
    /**
     * Message converters configuration
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setPrettyPrint(true);
        converters.add(jsonConverter);
    }
    
    /**
     * Formatter registry configuration
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Add custom formatters if needed
        // registry.addConverter(new StringToUserRoleConverter());
    }
    
    /**
     * View controller configuration
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect root to API documentation
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
        registry.addRedirectViewController("/api", "/swagger-ui/index.html");
    }
    
    /**
     * Interceptor configuration
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add custom interceptors if needed
        // registry.addInterceptor(new LoggingInterceptor());
    }
}