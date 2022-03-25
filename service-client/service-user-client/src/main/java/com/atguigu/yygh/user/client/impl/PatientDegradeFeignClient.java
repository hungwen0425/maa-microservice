package com.atguigu.yygh.user.client.impl;


import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.client.PatientFeignClient;
import org.springframework.stereotype.Component;

@Component
public class PatientDegradeFeignClient implements PatientFeignClient {


    @Override
    public Patient getPatient(Long id) {
        return null;
    }
}
