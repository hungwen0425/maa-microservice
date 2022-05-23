package com.atguigu.yygh.sms.receiver;

import com.atguigu.yygh.common.constant.MqConst;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SmsReceiver {

    @Autowired
    private SmsService smsService;

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
//            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
//            key = {MqConst.ROUTING_MSM_ITEM}
//    ))
//    public void send(MsmVo msmVo, Message message, Channel channel) {
//        smsService.send(msmVo);
//    }
}