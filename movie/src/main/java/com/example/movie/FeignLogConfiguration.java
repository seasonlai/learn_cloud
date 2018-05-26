package com.example.movie;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2018/5/26.
 */
@Configuration
public class FeignLogConfiguration {

    @Bean
    Logger.Level FeignLoggerLevel(){
        return Logger.Level.BASIC;
    }

}
