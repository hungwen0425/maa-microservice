package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.constant.MqConst;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.service.RabbitService;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.order.service.WeixinService;
import com.atguigu.yygh.user.client.HospitalFeignClient;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private PatientFeignClient patientFeignClient;

	@Autowired
	private HospitalFeignClient hospitalFeignClient;

	@Autowired
	private RabbitService rabbitService;

	@Autowired
	private WeixinService weixinService;
//
//	@Autowired
//	private PaymentService paymentService;
//
//	@Autowired
//	private RefundInfoService refundInfoService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Long saveOrder(String scheduleId, Long patientId) {
		Patient patient = patientFeignClient.getPatient(patientId);
		if(null == patient) {
			throw new YyghException(ResultCodeEnum.PARAM_ERROR);
		}
		ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
		if(null == scheduleOrderVo) {
			throw new YyghException(ResultCodeEnum.PARAM_ERROR);
		}

		//当前时间不可以预约
		if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
				|| new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
			throw new YyghException(ResultCodeEnum.TIME_NO);
		}

		SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());
		if(null == signInfoVo) {
			throw new YyghException(ResultCodeEnum.PARAM_ERROR);
		}

		if(scheduleOrderVo.getAvailableNumber() <= 0) {
			throw new YyghException(ResultCodeEnum.NUMBER_NO);
		}

		OrderInfo orderInfo = new OrderInfo();
		BeanUtils.copyProperties(scheduleOrderVo, orderInfo);

		String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
		orderInfo.setOutTradeNo(outTradeNo);
		orderInfo.setScheduleId(scheduleId);
		orderInfo.setUserId(patient.getUserId());
		orderInfo.setPatientId(patientId);
		orderInfo.setPatientName(patient.getName());
		orderInfo.setPatientPhone(patient.getPhone());
		orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
		this.save(orderInfo);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("hoscode",orderInfo.getHoscode());
		paramMap.put("depcode",orderInfo.getDepcode());
		paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
		paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
		paramMap.put("reserveTime", orderInfo.getReserveTime());
		paramMap.put("amount",orderInfo.getAmount());

		paramMap.put("name", patient.getName());
		paramMap.put("certificatesType",patient.getCertificatesType());
		paramMap.put("certificatesNo", patient.getCertificatesNo());
		paramMap.put("sex",patient.getSex());
		paramMap.put("birthdate", patient.getBirthdate());
		paramMap.put("phone",patient.getPhone());
		paramMap.put("isMarry", patient.getIsMarry());
		paramMap.put("provinceCode",patient.getProvinceCode());
		paramMap.put("cityCode", patient.getCityCode());
		paramMap.put("districtCode",patient.getDistrictCode());
		paramMap.put("address",patient.getAddress());
		//联系人
		paramMap.put("contactsName",patient.getContactsName());
		paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
		paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
		paramMap.put("contactsPhone",patient.getContactsPhone());
		paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

		String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
		paramMap.put("sign", sign);

		log.info("参数：" + JSONObject.toJSONString(paramMap));
		JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl()+"/order/submitOrder");
		log.info("结果：" + result.toJSONString());
		if(result.getInteger("code") == 200) {
			JSONObject jsonObject = result.getJSONObject("data");
			//预约记录唯一标识（医院预约记录主键）
			String hosRecordId = jsonObject.getString("hosRecordId");
			//预约序号
			Integer number = jsonObject.getInteger("number");;
			//取号时间
			String fetchTime = jsonObject.getString("fetchTime");;
			//取号地址
			String fetchAddress = jsonObject.getString("fetchAddress");;

			//更新订单
			orderInfo.setHosRecordId(hosRecordId);
			orderInfo.setNumber(number);
			orderInfo.setFetchTime(fetchTime);
			orderInfo.setFetchAddress(fetchAddress);
			this.updateById(orderInfo);

			//排班可预约数
			Integer reservedNumber = jsonObject.getInteger("reservedNumber");
			//排班剩余预约数
			Integer availableNumber = jsonObject.getInteger("availableNumber");

			//发送mq信息更新号源
			OrderMqVo orderMqVo = new OrderMqVo();
			orderMqVo.setScheduleId(scheduleId);
			orderMqVo.setReservedNumber(reservedNumber);
			orderMqVo.setAvailableNumber(availableNumber);

			//短信提示
			MsmVo msmVo = new MsmVo();
			msmVo.setPhone(orderInfo.getPatientPhone());
			msmVo.setTemplateCode("SMS_194640721");
			String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午" : "下午");
			Map<String,Object> param = new HashMap<String,Object>(){{
				put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
				put("amount", orderInfo.getAmount());
				put("reserveDate", reserveDate);
				put("name", orderInfo.getPatientName());
				put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
			}};
			msmVo.setParam(param);

			orderMqVo.setMsmVo(msmVo);
			rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
		} else {
			throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
		}

		return orderInfo.getId();
	}

	@Override
	public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
		IPage<OrderInfo> pages = orderInfoMapper.selectPage(pageParam, orderQueryVo);
		pages.getRecords().stream().forEach(item -> {
			this.packOrderInfo(item);
		});
		return pages;
	}

	private OrderInfo packOrderInfo(OrderInfo orderInfo) {
		orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
		return orderInfo;
	}

	@Override
	public OrderInfo getOrderInfo(Long orderId) {
		return this.packOrderInfo(this.getById(orderId));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean cancelOrder(Long orderId) {
		OrderInfo orderInfo = this.getById(orderId);

		//当前时间大约退号时间，不能取消预约
		DateTime quitTime = new DateTime(orderInfo.getQuitTime());
		if(quitTime.isBeforeNow()) {
			throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
		}

		SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
		if(null == signInfoVo) {
			throw new YyghException(ResultCodeEnum.PARAM_ERROR);
		}

		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("hoscode",orderInfo.getHoscode());
		reqMap.put("hosRecordId",orderInfo.getHosRecordId());
		reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
		String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
		reqMap.put("sign", sign);

		log.info("参数：" + JSONObject.toJSONString(reqMap));
		JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl()+"/order/updateCancelStatus");
		log.info("结果：" + result.toJSONString());
		if(result.getInteger("code") != 200) {
			throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
		} else {
			//是否支付 退款
			if(orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
				//已支付 退款
				boolean isRefund = weixinService.refund(orderId);
				if(!isRefund) {
					throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
				}
			}

			//更改订单状态
			orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
			this.updateById(orderInfo);

			//发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
			OrderMqVo orderMqVo = new OrderMqVo();
			orderMqVo.setScheduleId(orderInfo.getScheduleId());

			//短信提示
			MsmVo msmVo = new MsmVo();
			msmVo.setPhone(orderInfo.getPatientPhone());
			msmVo.setTemplateCode("SMS_194640722");
			String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午" : "下午");
			Map<String,Object> param = new HashMap<String,Object>(){{
				put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
				put("reserveDate", reserveDate);
				put("name", orderInfo.getPatientName());
			}};
			msmVo.setParam(param);

			orderMqVo.setMsmVo(msmVo);
			rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
		}
		return true;
	}

	@Override
	public Boolean confirmGetNumber(String hoscode, String hosRecordId) {
		QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("hoscode", hoscode);
		queryWrapper.eq("hosRecordId", hosRecordId);
		OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
		orderInfo.setOrderStatus(OrderStatusEnum.GET_NUMBER.getStatus());
		this.updateById(orderInfo);
		return true;
	}

	@Override
	public void patientTips() {
		QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
		List<OrderInfo> orderInfoList = orderInfoMapper.selectList(queryWrapper);
		for(OrderInfo orderInfo : orderInfoList) {
			//短信提示
			MsmVo msmVo = new MsmVo();
			msmVo.setPhone(orderInfo.getPatientPhone());
			msmVo.setTemplateCode("SMS_194610736");
			String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午" : "下午");
			Map<String,Object> param = new HashMap<String,Object>(){{
				put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
				put("reserveDate", reserveDate);
				put("name", orderInfo.getPatientName());
			}};
			msmVo.setParam(param);
			rabbitService.sendMessage(MqConst.QUEUE_MSM_ITEM, MqConst.ROUTING_MSM_ITEM, msmVo);
		}
	}

	@Override
	public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
		Map<String, Object> map = new HashMap<>();

		List<OrderCountVo> orderCountVoList = orderInfoMapper.selectOrderCount(orderCountQueryVo);
		//日期列表
		List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
		//统计列表
		List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());

		map.put("dateList", dateList);
		map.put("countList", countList);
		return map;
	}

	@Override
	public Map<String, Object> show(Long orderId) {
		Map<String, Object> map = new HashMap<>();

		OrderInfo orderInfo = this.packOrderInfo(this.getById(orderId));
		map.put("orderInfo", orderInfo);

		Patient patient = patientFeignClient.getPatient(orderInfo.getPatientId());
		map.put("patient", patient);
		return map;
	}

}
