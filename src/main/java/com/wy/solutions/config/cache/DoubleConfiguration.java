package com.wy.solutions.config.cache;

import com.wy.solutions.util.CacheUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author wangtoye
 * @date 2019-12-09
 * Description:
 */
@Configuration
public class DoubleConfiguration {

    /**
     * redis模板
     *
     * @param connectionFactory 连接工厂
     * @return redis操作实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        return new RedisTemplate<String, Object>() {
            {
                setConnectionFactory(connectionFactory);
                // 所有key使用 StringRedisSerializer
                StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
                setKeySerializer(stringRedisSerializer);
                setHashKeySerializer(stringRedisSerializer);
                // 所有value使用 GenericJackson2JsonRedisSerializer
                GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer =
                        CacheUtils.getGenericJackson2JsonRedisSerializer();
                setValueSerializer(genericJackson2JsonRedisSerializer);
                setHashValueSerializer(genericJackson2JsonRedisSerializer);
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
    public DoubleCacheManager cacheManager(
            RedisConnectionFactory connectionFactory, DoubleCacheProperties doubleCacheProperties,
            RedisTemplate redisTemplate) {
        //自定义redis缓存操作类-支持监听失效事件
        RedisCacheWriter redisCacheWriter = new RedisCallbackCacheWriter(connectionFactory, Duration.ofMillis(50),
                redisTemplate);
        //构造双缓存配置
        Map<String, DoubleCacheConfiguration> doubleCacheConfigurationMap =
                buildDoubleCacheConfigurationMap(doubleCacheProperties);

        return new DoubleCacheManager(redisCacheWriter,
                doubleCacheConfigurationMap,
                doubleCacheProperties.isAllowNullValues(), doubleCacheProperties.isUseL1Cache(),
                doubleCacheProperties.getTopic());
    }

    /**
     * 设置监听器容器
     *
     * @param redisTemplate         redis模板
     * @param doubleCacheManager    双缓存管理类
     * @param doubleCacheProperties 双缓存配置文件
     * @return 监听器容器
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisTemplate<String, Object> redisTemplate,
                                                                       DoubleCacheManager doubleCacheManager,
                                                                       DoubleCacheProperties doubleCacheProperties) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        DoubleCacheMessageListener doubleCacheMessageListener =
                new DoubleCacheMessageListener(redisTemplate, doubleCacheManager);
        redisMessageListenerContainer.addMessageListener(doubleCacheMessageListener,
                new ChannelTopic(doubleCacheProperties.getTopic()));
        return redisMessageListenerContainer;
    }


    /**
     * 构造doubleCacheConfiguration
     *
     * @param doubleCacheProperties 自定义的配置
     * @return doubleCacheConfiguration map
     */
    private Map<String, DoubleCacheConfiguration> buildDoubleCacheConfigurationMap(DoubleCacheProperties doubleCacheProperties) {
        // 默认配置
        CaffeineCacheConfiguration defaultCaffeineCacheConfig = doubleCacheProperties.isUseL1Cache() ?
                CaffeineCacheConfiguration
                        .defaultCacheConfig()
                        .setSpec("initialCapacity=10" +
                                ",maximumSize=50" +
                                ",expireAfterWrite=60s") : null;
        RedisCacheConfiguration defaultRedisCacheConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                // key 默认 StringRedisSerializer
                // value 默认 JdkSerializationRedisSerializer，此处修改为 GenericJackson2JsonRedisSerializer
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(CacheUtils.getGenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofSeconds(60))
                //默认两个::多了一层空文件夹
                .computePrefixWith(name -> name + ":")
                .disableCachingNullValues();

        return DoubleCacheConfiguration.buildDoubleCacheConfigurationMap(defaultCaffeineCacheConfig,
                defaultRedisCacheConfig,
                doubleCacheProperties);
    }
}
