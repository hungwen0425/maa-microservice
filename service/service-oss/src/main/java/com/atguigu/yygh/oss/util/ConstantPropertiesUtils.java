package com.atguigu.yygh.oss.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    private String endpoint = "oss-cn-hongkong.aliyuncs.com";

    private String keyId = "LTAI4GHDJxDbSZfWqyHE6UMh";

    private String keySecrect = "EQkQ0tQpXTYbZU3hY3h65iEACdz5CN";

    private String bucketName = "mma-tseng";

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRECT;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY_ID = keyId;
        ACCESS_KEY_SECRECT = keySecrect;
        BUCKET_NAME = bucketName;
    }
}
