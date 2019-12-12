package com.wy.solutions.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.wy.solutions.entity.User;
import com.wy.solutions.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangtoye
 * @since 2019-11-18
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Gson GSON = new Gson();

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "添加用户", notes = "分库分表添加")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void addUser(@RequestBody Integer age) {
        userService.save(new User() {{
            String time = System.currentTimeMillis() + "";
            this
                    .setUserId(Integer.parseInt(time.substring(time.length() - 6)))
                    .setAge(age)
                    .setName("测试" + getUserId())
                    .setEmail("ceshi" + getUserId() + "@gmail.com");
        }});
    }

    @ApiOperation(value = "查询所有用户", notes = "分库分表查询所有")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    public String queryAll() {
        return GSON.toJson(userService.list());
    }

    @ApiOperation(value = "查询指定用户", notes = "分库分表查询部分")
    @RequestMapping(value = "query", method = RequestMethod.GET)
    public String query(@RequestParam Integer userId, @RequestParam Integer age) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("age", age);
        return GSON.toJson(userService.list(qw));
    }

    @ApiOperation(value = "分页查询用户", notes = "分库分表查询分页")
    @RequestMapping(value = "queryByPage", method = RequestMethod.GET)
    public String queryByPage(@RequestParam Integer pageSize, @RequestParam Integer currentPage) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        IPage<User> pageData = userService.page(new Page<>(currentPage, pageSize), queryWrapper);
        return GSON.toJson(pageData.getRecords());
    }


    @ApiOperation(value = "查询指定用户", notes = "cache缓存测试")
    @RequestMapping(value = "queryByUserId", method = RequestMethod.GET)
    public String queryByUserId(@RequestParam Integer userId, @RequestParam Integer type) {
        if (type == 1) {
            return GSON.toJson(userService.getUserListByUserIdCacheable(userId));
        } else if (type == 2) {
            return GSON.toJson(userService.getUserListByUserIdCachePut(userId));
        } else if (type == 3) {
            return GSON.toJson(userService.cleanUserListByUserIdCacheEvict(userId));
        } else {
            return GSON.toJson(userService.getUserListByUserIdCaching(userId));
        }
    }
}