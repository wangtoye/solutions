package com.wy.solutions.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.solutions.entity.User;
import com.wy.solutions.mapper.UserMapper;
import com.wy.solutions.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangtoye
 * @since 2019-11-18
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private UserMapper userMapper;

    @Override
    @Cacheable(key = "#userId", value = "solutions:user", condition = "#userId%2==0")
    public List<User> getUserListByUserIdCacheable(Integer userId) {
        if (userId == null || userId == 0) {
            return null;
        }

        return userMapper.getUserListByUserId(userId);
    }

    @Override
    @CachePut(key = "#userId", value = "solutions:user1", condition = "#userId%2==0")
    public List<User> getUserListByUserIdCachePut(Integer userId) {
        if (userId == null || userId == 0) {
            return null;
        }

        return userMapper.getUserListByUserId(userId);
    }

    @Override
    @CacheEvict(key = "#userId", value = "solutions:user", condition = "#userId%2==0", beforeInvocation = true)
    public String cleanUserListByUserIdCacheEvict(Integer userId) {
        return "清理" + userId + "的缓存成功";
    }


    @Override
    @Caching(
            cacheable = {@Cacheable(key = "#userId", value = "solutions:user2", condition = "#userId%2==0")},
            put = {@CachePut(key = "#result[0].id", value = "solutions:user2", condition = "#userId%2==0")}
    )
    public List<User> getUserListByUserIdCaching(Integer userId) {
        if (userId == null || userId == 0) {
            return null;
        }

        return userMapper.getUserListByUserId(userId);
    }
}
