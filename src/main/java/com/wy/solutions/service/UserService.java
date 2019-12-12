package com.wy.solutions.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wy.solutions.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangtoye
 * @since 2019-11-18
 */
public interface UserService extends IService<User> {

    /**
     * 通过用户id查询用户列表
     * 使用 Cacheable
     *
     * @param userId 用户id
     * @return 用户列表
     */
    List<User> getUserListByUserIdCacheable(Integer userId);

    /**
     * 通过用户id查询用户列表
     * 使用 CachePut
     *
     * @param userId 用户id
     * @return 用户列表
     */
    List<User> getUserListByUserIdCachePut(Integer userId);


    /**
     * 清理指定key的缓存
     * 使用 CacheEvict
     *
     * @param userId 用户id
     * @return 用户列表
     */
    String cleanUserListByUserIdCacheEvict(Integer userId);


    /**
     * 通过用户id查询用户列表
     * 使用 Caching
     *
     * @param userId 用户id
     * @return 用户列表
     */
    List<User> getUserListByUserIdCaching(Integer userId);
}
