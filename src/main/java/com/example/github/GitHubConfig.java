package com.example.github;

import org.kohsuke.github.GitHub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GitHubConfig {
    @Bean
    public GitHub gitHub() {
        return GitHub.offline();
    }
}
