package com.nly;

import com.nly.Controller.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Configuration
/**
 * 重写WebMvcConfigurerAdapter
 * 用于加载静态资源
 */
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    //实现资源关联
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //映射classpath，以及本地路径
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:C:/G/Nly-videos-dev/");

    }

    //实现对拦截器进行注册
    @Bean
    public MiniInterceptor miniInterceptor(){
        return new MiniInterceptor();
    }

    //InterceptorRegistry 注册中心
    //user下使用通配符：**将所有的方法都进行拦截
    //bgm的list需要拦截
    //video需要对多个方法(upload，uploadCover)进行拦截。数组形式，将单个单个进行拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(miniInterceptor())
        .addPathPatterns("/user/**")
        .addPathPatterns("/bgm/**")
        .addPathPatterns("/video/upload","/video/uploadCover","video/userLike","userUnLike")
        .excludePathPatterns("/user/queryPublisher");

        super.addInterceptors(registry);
    }
}
