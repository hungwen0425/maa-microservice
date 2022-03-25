package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

;import java.util.Map;

public interface OrderService extends IService<OrderInfo> {

	/**
	 * 保存订单
	 * @param scheduleId
	 * @param patientId
	 * @return
	 */
	Long saveOrder(String scheduleId, Long patientId);

	/**
	 * 分页列表
	 * @param pageParam
	 * @param orderQueryVo
	 * @return
	 */
	IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

	/**
	 * 获取订单详情
	 * @param orderId
	 * @return
	 */
	OrderInfo getOrderInfo(Long orderId);

	/**
	 * 取消预约
	 * @param orderId
	 */
	Boolean cancelOrder(Long orderId);

	/**
	 * 确认取号
	 * @param hoscode
	 * @param hosRecordId
	 * @return
	 */
	Boolean confirmGetNumber(String hoscode, String hosRecordId);

	/**
	 * 就诊提醒
	 * 第二天就诊，前一天提醒
	 */
	void patientTips();

	/**
	 * 订单统计
	 * @param orderCountQueryVo
	 * @return
	 */
	Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

	/**
	 * 订单详情
	 * @param orderId
	 * @return
	 */
	Map<String,Object> show(Long orderId);
}
