package com.atguigu.yygh.user.mapper;

import com.atguigu.yygh.model.user.UserLoginRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {

    IPage<UserLoginRecord> selectPage(Page<UserLoginRecord> page, @Param("userId") Long userId);
}
