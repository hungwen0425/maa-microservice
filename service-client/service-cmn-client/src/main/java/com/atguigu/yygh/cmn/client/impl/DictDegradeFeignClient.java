package com.atguigu.yygh.cmn.client.impl;


import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DictDegradeFeignClient implements DictFeignClient {

    @Override
    public String getName(String parentDictCode, String value) {
        return null;
    }

    @Override
    public String getName(String value) {
        return null;
    }
}
