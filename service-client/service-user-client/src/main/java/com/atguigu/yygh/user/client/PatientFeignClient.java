package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.client.impl.PatientDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 会员用户API接口
 * </p>
 *
 * @author qy
 */
@FeignClient(value = "service-user", fallback = PatientDegradeFeignClient.class)
public interface PatientFeignClient {

    @GetMapping("/api/user/patient/inner/get/{id}")
    Patient getPatient(@PathVariable("id") Long id);

}