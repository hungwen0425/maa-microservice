package com.atguigu.yygh.sms.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.util.RandomUtil;
import com.atguigu.yygh.vo.msm.MsmVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * qy
 */
@Api(tags = "短信接口")
@RestController
@RequestMapping("/api/sms")
@Slf4j
public class SmsApiController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation(value = "获取验证码")
    @GetMapping(value = "/send/{phone}")
    public Result code(
            @ApiParam(name = "phone", value = "手机", required = true)
            @PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) return Result.ok();

        code = RandomUtil.getSixBitRandom();
        Map<String,Object> param = new HashMap<>();
        param.put("code", code);
        boolean isSend = smsService.send(phone, "SMS_187220125", param);
        if(isSend) {
            redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.MINUTES);
            return Result.ok();
        }
        return Result.fail().message("发送短信失败");
    }
}
