package com.atguigu.yygh.order.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.WeixinService;
import com.atguigu.yygh.order.util.StreamUtil;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * 微信支付 API
 * </p>
 *
 * @author qy
 */
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/order/weixin")
@Slf4j
public class WeixinController {

    @Autowired
    private WeixinService weixinPayService;

    @Autowired
    private PaymentService paymentService;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @ApiOperation(value = "下单 生成二维码")
    @GetMapping("/createNative/{orderId}")
    public Result createNative(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        return Result.ok(weixinPayService.createNative(orderId));
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        //调用查询接口
        Map<String, String> resultMap = weixinPayService.queryPayStatus(orderId, PaymentTypeEnum.WEIXIN.name());
        if (resultMap == null) {//出错
            return Result.fail().message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {//如果成功
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.paySuccess(out_trade_no, PaymentTypeEnum.WEIXIN.getStatus(), resultMap);
            return Result.ok().message("支付成功");
        }

        return Result.ok().message("支付中");
    }

    /**
     * 该链接是通过【统一下单API】中提交的参数notify_url设置，如果链接无法访问，商户将无法接收到微信通知。
     * 通知url必须为直接可访问的url，不能携带参数。示例：notify_url：“https://pay.weixin.qq.com/wxpay/pay.action”
     * <p>
     * 支付完成后，微信会把相关支付结果和用户信息发送给商户，商户需要接收处理，并返回应答。
     * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功。
     * （通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
     * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
     * 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，如果没有处理过再进行处理，如果处理过直接返回结果成功。在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 特别提醒：商户系统对于支付结果通知的内容一定要做签名验证，防止数据泄漏导致出现“假通知”，造成资金损失。
     *
     */
    @ApiOperation(value = "微信支付|支付回调接口", httpMethod = "POST", notes = "该链接是通过【统一下单API】中提交的参数notify_url设置，如果链接无法访问，商户将无法接收到微信通知。")
    @RequestMapping("/notify")
    public void wxnotify(HttpServletRequest request, HttpServletResponse response) {
        String resXml = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml>";
        try {
            String xmlString = StreamUtil.inputStream2String(request.getInputStream(),"utf-8");
            log.info("wxnotify:微信支付----result----=" + xmlString);

            // xml转换为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlString);
            if (WXPayUtil.isSignatureValid(resultMap, partnerkey)) {
                log.info("wxnotify:微信支付----返回成功");
                if (WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                    //更改订单状态
                    //weixinPayService.updateOrderStatus(resultMap);
                    String out_trade_no = resultMap.get("out_trade_no");
                    paymentService.paySuccess(out_trade_no, PaymentTypeEnum.WEIXIN.getStatus(), resultMap);

                    log.info("wxnotify:微信支付----验证签名成功");

                    // 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                    resXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                } else {
                    log.error("wxnotify:支付失败,错误信息：" + resultMap.get("err_code_des"));
                }
            } else {
                log.error("wxnotify:微信支付----判断签名错误");
            }
        } catch (Exception e) {
            log.error("wxnotify:支付回调发布异常：", e);
        } finally {
            try {
                // 处理业务完毕
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                log.error("wxnotify:支付回调发布异常:out：", e);
            }
        }
    }
}
