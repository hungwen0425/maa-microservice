package com.atguigu.yygh.hosp.mapper;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {

    IPage<HospitalSet> selectPage(Page<HospitalSet> page, @Param("vo") HospitalSetQueryVo hospitalSetQueryVo);
}
