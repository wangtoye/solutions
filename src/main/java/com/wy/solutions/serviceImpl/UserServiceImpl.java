package com.wy.solutions.serviceImpl;

import com.wy.solutions.entity.User;
import com.wy.solutions.mapper.UserMapper;
import com.wy.solutions.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangye
 * @since 2019-11-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
