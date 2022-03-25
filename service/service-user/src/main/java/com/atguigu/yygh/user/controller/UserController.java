package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.model.user.UserLoginRecord;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户接口
 * </p>
 *
 * @author qy
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "获取用户分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "userInfoVo", value = "查询对象", required = false)
                    UserInfoQueryVo userInfoVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam, userInfoVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public Result lock(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @PathVariable("userId") Long userId,

            @ApiParam(name = "status", value = "锁定状态（0：锁定 1：解锁）", required = true)
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("show/{userId}")
    public Result show(@PathVariable("userId") Long userId){
        return Result.ok(userInfoService.show(userId));
    }

    @ApiOperation(value = "认证审批")
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @PathVariable("userId") Long userId,

            @ApiParam(name = "authStatus", value = "认证状态状态（2：通过 -1：不通过）", required = true)
            @PathVariable("authStatus") Integer authStatus){
        userInfoService.approval(userId, authStatus);
        return Result.ok();
    }
}
