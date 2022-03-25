package com.atguigu.yygh.common.util;

import com.atguigu.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取登录用户信息类
 *
 * @author qy
 */
public class AuthContextHolder {

    /**
     * 获取当前登录用户id
     * @param request
     * @return
     */
    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    /**
     * 获取当前登录用户名称
     * @param request
     * @return
     */
    public static String getUserName(HttpServletRequest request) {
        String token = request.getHeader("token");
        String userName = JwtHelper.getUserName(token);
        return userName;
    }

}
