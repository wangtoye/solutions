package com.wy.solutions.config.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author wangtoye
 * @date 2019-12-10
 * Description:
 */
public class DoubleCacheLoader implements CacheLoader<Object, Object> {

    @Nullable
    @Override
    public Object load(@NonNull Object key) throws Exception {
        System.out.println(key);
        return null;
    }

    /**
     * 如果配置refreshAfterWrite则需要这个方法
     *
     * @param key      键
     * @param oldValue 旧值
     * @return 值
     */
    @Nullable
    @Override
    public Object reload(@NonNull Object key, @NonNull Object oldValue) {
        System.out.println(key);
        System.out.println(oldValue);
        return oldValue;
    }
}
