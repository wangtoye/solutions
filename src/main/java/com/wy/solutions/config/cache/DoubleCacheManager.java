package com.wy.solutions.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author wangtoye
 * @date 2019-12-05
 * Description: 管理 caffeine 和 redis 多级缓存
 */
public class DoubleCacheManager extends AbstractTransactionSupportingCacheManager {
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCacheWriter;
    private DoubleCacheLoader doubleCacheLoader;
    private RedisCacheWriter redisCacheWriter;
    private Map<String, DoubleCacheConfiguration> initialCacheConfiguration;
    private boolean allowNullValues;
    private boolean useL1Cache;
    private String topic;
    public static List<String> uuidList = Collections.synchronizedList(new ArrayList<>());

    /**
     * @param redisCacheWriter          redis操作缓存的工具
     * @param initialCacheConfiguration 配置
     * @param allowNullValues           是否允许存储null值
     * @param useL1Cache                是否开启一级缓存
     * @param topic                     发布订阅的主题
     */
    public DoubleCacheManager(
            RedisCacheWriter redisCacheWriter,
            Map<String, DoubleCacheConfiguration> initialCacheConfiguration,
            boolean allowNullValues, boolean useL1Cache, String topic) {
        this.redisCacheWriter = redisCacheWriter;
        this.initialCacheConfiguration = initialCacheConfiguration;
        this.allowNullValues = allowNullValues;
        this.useL1Cache = useL1Cache;
        this.topic = topic;
    }

    /**
     * 导入配置信息，构造configuration
     *
     * @return Cache
     */
    @Override
    protected Collection<? extends Cache> loadCaches() {
        List<DoubleCache> caches = new LinkedList<>();
        for (Map.Entry<String, DoubleCacheConfiguration> entry : initialCacheConfiguration.entrySet()) {
            caches.add(createDoubleCache(entry.getKey(), entry.getValue()));
        }
        return caches;
    }

    /**
     * 创建一个默认的cache
     *
     * @param name cacheName
     * @return doubleCache
     */
    @Override
    protected Cache getMissingCache(String name) {
        if (this.useL1Cache) {
            //如果开启了一级缓存则取默认配置构造缓存操作类
            if (doubleCacheLoader == null) {
                doubleCacheLoader = new DoubleCacheLoader();
            }
            caffeineCacheWriter =
                    Caffeine.from(CaffeineCacheConfiguration.defaultCacheConfig().getSpec()).build(doubleCacheLoader);
        } else {
            caffeineCacheWriter = null;
        }

        return new DoubleCache(name, caffeineCacheWriter, redisCacheWriter,
                DoubleCacheConfiguration.defaultCacheConfig(),
                allowNullValues, topic);
    }

    /**
     * 创建cache
     *
     * @param name                     cacheName
     * @param doubleCacheConfiguration 配置
     * @return doubleCache
     */
    private DoubleCache createDoubleCache(String name, DoubleCacheConfiguration doubleCacheConfiguration) {
        CaffeineCacheConfiguration caffeineCacheConfiguration =
                doubleCacheConfiguration.getCaffeineCacheConfiguration();
        if (this.useL1Cache) {
            //根据caffeine的配置构造一个cacheWriter
            if (caffeineCacheConfiguration == null) {
                caffeineCacheWriter = Caffeine.newBuilder().build();
            } else {
                //此处构造需要一个cacheLoader，因为入参中可能会包含refreshAfterWrite属性
                if (doubleCacheLoader == null) {
                    doubleCacheLoader = new DoubleCacheLoader();
                }
                caffeineCacheWriter = Caffeine.from(caffeineCacheConfiguration.getSpec()).build(doubleCacheLoader);
            }
        } else {
            caffeineCacheWriter = null;
        }

        return new DoubleCache(name, caffeineCacheWriter, redisCacheWriter, doubleCacheConfiguration,
                allowNullValues, topic);
    }


    /**
     * 清除指定cache的值
     *
     * @param cacheName cacheName
     * @param key       键
     */
    public void clearL1Cache(String cacheName, Object key) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            ((DoubleCache) cache).clearL1Cache(key);
        }
    }
}
