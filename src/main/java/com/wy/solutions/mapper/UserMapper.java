package com.wy.solutions.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wy.solutions.entity.User;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangtoye
 * @since 2019-11-18
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 通过用户id查询用户列表
     *
     * @param userId 用户id
     * @return 用户列表
     */
    List<User> getUserListByUserId(Integer userId);
}