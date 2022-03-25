package com.atguigu.yygh.common.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description 消息发送确认
 * <p>
 * ConfirmCallback  只确认消息是否正确到达 Exchange 中
 * ReturnCallback   消息没有正确到达队列时触发回调，如果正确到达队列不执行
 * <p>
 * 1. 如果消息没有到exchange,则confirm回调,ack=false
 * 2. 如果消息到达exchange,则confirm回调,ack=true
 * 3. exchange到queue成功,则不回调return
 * 4. exchange到queue失败,则回调return
 * @Author qy
 */
//@Component
//@Slf4j
//public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @PostConstruct
//    public void init() {
//        rabbitTemplate.setConfirmCallback(this);            //指定 ConfirmCallback
//        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback
//    }
//
//    @Override
//    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//        if (ack) {
//            log.info("消息发送成功：" + JSON.toJSONString(correlationData));
//        } else {
//            log.info("消息发送失败：" + cause + " 数据：" + JSON.toJSONString(correlationData));
//        }
//    }
//
//    @Override
//    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//        // 反序列化对象输出
//        System.out.println("消息主体: " + new String(message.getBody()));
//        System.out.println("应答码: " + replyCode);
//        System.out.println("描述：" + replyText);
//        System.out.println("消息使用的交换器 exchange : " + exchange);
//        System.out.println("消息使用的路由键 routing : " + routingKey);
//        System.out.println("CorrelationId  -> " + message.getMessageProperties().getHeaders().get("spring_returned_message_correlation"));
//    }
//}
