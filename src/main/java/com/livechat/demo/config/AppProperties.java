package com.livechat.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
@Getter
@Setter
public class AppProperties {
    private String verifyToken;
    private String pageAccessToken;
    private String natsConfig;
}
