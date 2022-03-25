package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HospitalSetService extends IService<HospitalSet> {

    IPage<HospitalSet> selectPage(Page<HospitalSet> pageParam, HospitalSetQueryVo hospitalSetQueryVo);

    /**
     * 锁定
     * @param id
     * @param status 0：锁定 1：正常
     */
    void lock(String id, Integer status);

    /**
     * 发送签名key
     * @param id
     */
    void sendSignKey(String id);

    /**
     * 获取签名key
     * @param hoscode
     * @return
     */
    String getSignKey(String hoscode);

    /**
     * 获取医院签名信息
     * @param hoscode
     * @return
     */
    SignInfoVo getSignInfoVo(String hoscode);
}
