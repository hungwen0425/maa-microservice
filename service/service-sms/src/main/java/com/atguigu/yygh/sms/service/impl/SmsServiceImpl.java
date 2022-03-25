package com.atguigu.yygh.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.util.ConstantPropertiesUtils;
import com.atguigu.yygh.vo.msm.MsmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {


    /**
     * 发送短信
     * @param PhoneNumbers
     * @param templateCode
     * @param param
     * @return
     */
    @Override
    public boolean send(String PhoneNumbers, String templateCode, Map<String,Object> param) {

        if(StringUtils.isEmpty(PhoneNumbers)) return false;

        DefaultProfile profile = DefaultProfile.getProfile(ConstantPropertiesUtils.REGION_Id, ConstantPropertiesUtils.ACCESS_KEY_ID, ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        request.putQueryParameter("PhoneNumbers", PhoneNumbers);
        request.putQueryParameter("SignName", "尚硅谷");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param msmVo
     * @return
     */
    @Override
    public boolean send(MsmVo msmVo) {
        log.info(JSONObject.toJSONString(msmVo));
        if(!StringUtils.isEmpty(msmVo.getPhone())) {
            return this.send(msmVo.getPhone(), msmVo.getTemplateCode(), msmVo.getParam());
        }
        return false;
    }
}
