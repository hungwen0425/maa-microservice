package com.atguigu.yygh.user.client.impl;


import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.user.client.HospitalFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import org.springframework.stereotype.Component;

@Component
public class HospitalDegradeFeignClient implements HospitalFeignClient {


    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        return null;
    }

    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        return null;
    }
}
