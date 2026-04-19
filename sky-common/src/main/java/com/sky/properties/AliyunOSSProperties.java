package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliyunOSSProperties {

    private String endpoint;
    private String bucketName;
    private String region;

}
