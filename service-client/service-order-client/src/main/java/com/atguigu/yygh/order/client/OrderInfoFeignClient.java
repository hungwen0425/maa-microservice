package com.atguigu.yygh.order.client;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.order.client.impl.OrderInfoDegradeFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * <p>
 * 订单API接口
 * </p>
 *
 * @author qy
 */
@FeignClient(value = "service-order", fallback = OrderInfoDegradeFeignClient.class)
public interface OrderInfoFeignClient {

    /**
     * 确认取号
     * @param hoscode
     * @param resNo
     * @return
     */
    @GetMapping("/api/order/orderInfo/inner/confirmGetNumber/{hoscode}/{resNo}")
    Boolean confirmGetNumber(@PathVariable("hoscode") String hoscode, @PathVariable("resNo") String resNo);

    /**
     * 获取订单统计数据
     * @param orderCountQueryVo
     * @return
     */
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);

}