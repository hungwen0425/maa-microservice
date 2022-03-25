package com.atguigu.yygh.order.client.impl;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.client.OrderInfoFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderInfoDegradeFeignClient implements OrderInfoFeignClient {


    @Override
    public Boolean confirmGetNumber(String hoscode, String resNo) {
        return false;
    }

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        return null;
    }
}
