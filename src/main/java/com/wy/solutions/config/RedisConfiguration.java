package com.wy.solutions.config;

import com.wy.solutions.util.CacheUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 启用SpringCache 使用RedisCacheManager做缓存控制器
 *
 * @see org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
 */
//@Configuration
public class RedisConfiguration {

    /**
     * 默认保存1800s，也就是30分钟
     */
    @Value("${solutions.redis.default.expriation:1800}")
    private Integer expiration;

    /**
     * 1. 默认 JdkSerializationRedisSerializer - JDK提供的序列化功能
     * 优点是反序列化时不需要提供类型信息(class)，但缺点是序列化后的结果非常庞大，
     * 是JSON格式的5倍左右，这样就会消耗redis服务器的大量内存
     * 速度仅仅比json稍快,所以优先考虑json序列化
     * <p>
     * 2. Jackson2JsonRedisSerializer 必须提供类型信息
     * 使用Jackson库将对象序列化为JSON字符串。优点是速度快，序列化后的字符串短小精悍。
     * 但缺点也非常致命，那就是此类的构造函数中有一个类型参数，必须提供要序列化对象的类型信息(.class对象)
     * 通过查看源代码，发现其只在反序列化过程中用到了类型信息
     * <p>
     * 3. GenericJackson2JsonRedisSerializer 解决 2 不需要传类型参数
     * <p>
     * 4. protostuff  比Jackson性能好，空间少的方式，value为二进制方式  --  jpa的代理对象无法被序列化二进制
     * *** 可读性略差, 底层为 Unsafe 反射拷贝！所以遇见 hibernate 代理对象就要炸！
     * 由google提供
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        return new RedisTemplate() {
            {
                setConnectionFactory(connectionFactory);
                // 所有key使用 StringRedisSerializer
                setKeySerializer(new StringRedisSerializer());
                // 其他的序列化使用GenericJackson2JsonRedisSerializer
                GenericJackson2JsonRedisSerializer serializer = CacheUtils.getGenericJackson2JsonRedisSerializer();
                setValueSerializer(serializer);
                setHashKeySerializer(serializer);
                setHashValueSerializer(serializer);
                afterPropertiesSet();
            }
        };
    }

    /**
     * 使用Spring CacheManager管理缓存
     *
     * @return RedisCacheManager 配置
     */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory, MyRedisProperties myRedisProperties) {

        // 配置序列化策略
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                // key 默认 StringRedisSerializer
                // value 默认 JdkSerializationRedisSerializer，此处修改为 GenericJackson2JsonRedisSerializer
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(CacheUtils.getGenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofSeconds(expiration))
                //默认两个::多了一层空文件夹
                .computePrefixWith(name -> name + ":")
                .disableCachingNullValues();

        RedisCacheManager.RedisCacheManagerBuilder managerBuilder = RedisCacheManager
                .builder(RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultCacheConfig);

        //针对不同的key配置ttl
        Map<String, CacheProperties.Redis> customCachePropertiesMap = myRedisProperties.getCustomCache();
        Map<String, RedisCacheConfiguration> ttlConfigMap = new HashMap<>(customCachePropertiesMap.size());
        Optional.ofNullable(myRedisProperties).map(p -> p.getCustomCache()).ifPresent(customCache -> {
            customCache.forEach((key, redisConfig) -> {
                RedisCacheConfiguration customCacheConfig = CacheUtils.handleRedisCacheConfiguration(redisConfig,
                        defaultCacheConfig);
                ttlConfigMap.put(key, customCacheConfig);
            });
        });

        managerBuilder.withInitialCacheConfigurations(ttlConfigMap);
        return managerBuilder.build();
    }

    /**
     * 缺省key的生成规则
     *
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName()).append(":").append(method.getName()).append(":");
            //这边是直接把入参拼接，可以做一定的修改让key更优美。
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}
