package com.atguigu.yygh.sms.service;


import com.atguigu.yygh.vo.msm.MsmVo;
import java.util.Map;

/**
 *
 */
public interface SmsService {

    boolean send(String phone, String content, String code);

    boolean sendMma(MsmVo msmVo);

}
