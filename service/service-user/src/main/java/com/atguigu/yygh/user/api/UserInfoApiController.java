package com.atguigu.yygh.user.api;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.util.AuthContextHolder;
import com.atguigu.yygh.common.util.IpUtil;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.RegisterVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户接口
 * </p>
 *
 * @author qy
 */
@Api(tags = "会员接口")
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        loginVo.setIp(IpUtil.getIpAddr(request));
        Map<String, Object> info = userInfoService.login(loginVo);
        return Result.ok(info);
    }

    @ApiOperation(value = "会员认证")
    @PostMapping("auth/userAuah")
    public Result userAuah(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        userInfoService.userAuah(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    @ApiOperation(value = "获取会员信息")
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        return Result.ok(userInfoService.getById(userId));
    }
}
