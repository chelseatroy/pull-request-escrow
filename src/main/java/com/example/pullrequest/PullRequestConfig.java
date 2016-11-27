package com.example.pullrequest;

import com.example.github.GHEventPayloadPullRequestArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class PullRequestConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private GHEventPayloadPullRequestArgumentResolver ghEventPayloadPullRequestArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(ghEventPayloadPullRequestArgumentResolver);
    }
}