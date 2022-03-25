package com.atguigu.yygh.sms.service;


import com.atguigu.yygh.vo.msm.MsmVo;
import java.util.Map;

/**
 *
 */
public interface SmsService {

    boolean send(String PhoneNumbers, String templateCode, Map<String,Object> param);

    boolean send(MsmVo msmVo);
}
