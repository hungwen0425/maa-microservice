package com.atguigu.yygh.user.mapper;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    IPage<UserInfo> selectPage(Page<UserInfo> page, @Param("vo") UserInfoQueryVo userInfoVo);
}