package com.cwb.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Configuration
public class RestTemplateConfig {
    @Bean
    RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        return  restTemplate;
    }
}
