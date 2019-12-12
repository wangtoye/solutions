package com.wy.solutions.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author wangtoye
 * @date 2019-12-04
 * Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "solutions.cache")
public class MyRedisProperties {

    private Map<String, CacheProperties.Redis> customCache;
}
