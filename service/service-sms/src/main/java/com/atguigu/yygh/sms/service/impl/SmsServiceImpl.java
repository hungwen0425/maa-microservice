package com.atguigu.yygh.sms.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.sms.service.SmsService;

import com.atguigu.yygh.vo.msm.MsmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${mma.sms.sendSMSUrl}")
    private String sendSMSUrl;
    @Value("${mma.sms.getCreditUrl}")
    private String getCreditUrl;
//    @Value("${mma.sms.userID}")
    private String userID = "0986052168";
    @Value("${mma.sms.password}")
    private String password;
    @Value("${mma.sms.credit}")
    private double credit;
    @Value("${mma.sms.subject}")
    private String subject;

    String processMsg = "";
    String batchID = "";
    String sendTime = "";
    public boolean send(String phone, String code) {

        String subject = "";
        String content = "雲端商城手機註冊驗證碼：";
        String sendTime = "";

        if (this.getCredit(userID, password)) {
            System.out.println(new StringBuffer("取得餘額成功，餘額：").append(String.valueOf(String.valueOf(credit))));
        } else {
            System.out.println(new StringBuffer("取得餘額失敗，失敗原因：").append(processMsg));
        }
        if (this.sendSMS(userID, password, subject, content + code, phone, sendTime)) {
            System.out.println(new StringBuffer("發送簡訊成功，餘額：").append(String.valueOf(credit)).append("，簡訊批號：").append(batchID));
            return true;
        } else {
            System.out.println(new StringBuffer("發送簡訊失敗，失敗原因：").append(processMsg));
            return false;
        }

    }

    /**
     * 傳送簡訊
     * @param userID 帳號
     * @param password 密碼
     * @param subject 簡訊主旨，主旨不會隨著簡訊內容發送出去。用以註記本次發送之用途。可傳入空字串
     * @param content 簡訊發送內容
     * @param mobile 接收人之手機號碼。格式為: +886912345678或09123456789。多筆接收人時，請以半形逗點隔開( , )，如0912345678,0922333444
     * @param sendTime 簡訊預定發送時間。-立即發送：請傳入空字串。-預約發送：請傳入預計發送時間，若傳送時間小於系統接單時間，將不予傳送。
     *                 格式為 YYYYMMDDhhmnss；例如:預約 2009/01/31 15:30:00發送，則傳入20090131153000。若傳歸時間已逾現在之時間，將立即發送。
     * @return true:傳送成功；false:傳送失敗
     */
    public boolean sendSMS(String userID, String password, String subject, String content, String mobile, String sendTime) {
        boolean success = false;
        try {
            StringBuilder postDataSb = new StringBuilder();
            postDataSb.append("UID=").append(userID);
            postDataSb.append("&PWD=").append(password);
            postDataSb.append("&SB=").append(subject);
            postDataSb.append("&MSG=").append(content);
            postDataSb.append("&DEST=").append(mobile);
            postDataSb.append("&ST=").append(sendTime);

            String resultString = httpPost(sendSMSUrl, postDataSb.toString());
            if (!resultString.startsWith("-")) {

                String[] split = resultString.split(",");
                this.credit = Double.parseDouble(split[0]);
                batchID = split[4];
                success = true;
            } else {
                processMsg = resultString;
            }

        } catch (Exception ex) {
            processMsg = ex.getMessage();
        }
        return success;
    }

    /**
     * 取得帳號餘額
     * @param userID 帳號
     * @param password 密碼密碼
     * @return true:取得成功；false:取得失敗
     */
    public boolean getCredit(String userID, String password) {
        boolean success = false;
        try {
            StringBuilder postDataSb = new StringBuilder();
            postDataSb.append("UID=").append(userID);
            postDataSb.append("&PWD=").append(password);

            String resultString = this.httpPost(getCreditUrl, postDataSb.toString());
            if (!resultString.startsWith("-")) {
                credit = Double.parseDouble(resultString);
                success = true;
            } else {
                processMsg = resultString;
            }
        } catch (Exception ex) {
            processMsg = ex.getMessage();
        }
        return success;
    }

    private String httpPost(String url, String postData) {
        String result = "";
        try {
            URL u=new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            osw.write(postData);
            osw.flush();
            osw.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder sb=new StringBuilder();
            for (line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
            }
            conn.disconnect();
            result=sb.toString();
        } catch (Exception ex) {
            processMsg = ex.getMessage();
        }
        return result;
    }

    /**
     *
     * @param msmVo
     * @return
     */
//    @Override
//    public boolean send(MsmVo msmVo) {
//        log.info(JSONObject.toJSONString(msmVo));
//        if(!StringUtils.isEmpty(msmVo.getPhone())) {
//            return this.send(msmVo.getPhone(), msmVo.getTemplateCode(), msmVo.getParam());
//        }
//        return false;
//    }
}
