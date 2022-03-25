package com.atguigu.yygh.order.api;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.util.AuthContextHolder;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.user.client.HospitalFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qy
 *
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
@Slf4j
public class OrderApiController {

	@Autowired
	private OrderService orderService;

	public OrderApiController(){
		initRule();
	}

	/**
	 * 导入热点值限流规则
	 * 也可在Sentinel dashboard界面配置（仅测试）
	 */
	public void initRule() {
		ParamFlowRule pRule = new ParamFlowRule("submitOrder")//资源名称，与SentinelResource值保持一致
				//限流第一个参数
				.setParamIdx(0)
				//单机阈值
				.setCount(5);

		// 针对 热点参数值单独设置限流 QPS 阈值，而不是全局的阈值.
		//如：1000_0（北京协和医院）,可以通过数据库表一次性导入，目前为测试
		ParamFlowItem item1 = new ParamFlowItem().setObject("1000_0")//热点值
				.setClassType(String.class.getName())//热点值类型
				.setCount(1);//热点值 QPS 阈值
		List<ParamFlowItem> list = new ArrayList<>();
		list.add(item1);
		pRule.setParamFlowItemList(list);
		ParamFlowRuleManager.loadRules(Collections.singletonList(pRule));
	}

	@ApiOperation(value = "创建订单")
	@PostMapping("auth/submitOrder/{hoscode}/{scheduleId}/{patientId}")
	@SentinelResource(value = "submitOrder",blockHandler = "submitOrderBlockHandler")
	public Result submitOrder(
			@ApiParam(name = "hoscode", value = "医院编号，限流使用", required = true)
			@PathVariable String hoscode,
			@ApiParam(name = "scheduleId", value = "排班id", required = true)
			@PathVariable String scheduleId,
			@ApiParam(name = "patientId", value = "就诊人id", required = true)
			@PathVariable Long patientId) {
		return Result.ok(orderService.saveOrder(scheduleId, patientId));
	}

	/**
	 * 热点值超过 QPS 阈值，返回结果
	 * @param hoscode
	 * @param scheduleId
	 * @param patientId
	 * @param e
	 * @return
	 */
	public Result submitOrderBlockHandler(String hoscode, String scheduleId, Long patientId, BlockException e){
		return Result.build(null,ResultCodeEnum.ORDER_CREATE_ERROR);
	}

	@ApiOperation(value = "获取分页列表")
	@GetMapping("auth/{page}/{limit}")
	public Result index(
		@ApiParam(name = "page", value = "当前页码", required = true)
		@PathVariable Long page,

		@ApiParam(name = "limit", value = "每页记录数", required = true)
		@PathVariable Long limit,

		@ApiParam(name = "orderQueryVo", value = "查询对象", required = false)
				OrderQueryVo orderQueryVo, HttpServletRequest request) {
		orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
		Page<OrderInfo> pageParam = new Page<>(page, limit);
		IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
		return Result.ok(pageModel);
	}

	@ApiOperation(value = "获取订单状态")
	@GetMapping("auth/getStatusList")
	public Result getStatusList() {
		return Result.ok(OrderStatusEnum.getStatusList());
	}

	@ApiOperation(value = "获取订单")
	@GetMapping("auth/getOrderInfo/{id}")
	public Result getOrderInfo(
			@ApiParam(name = "id", value = "订单id", required = true)
			@PathVariable Long id) {
		OrderInfo order = orderService.getOrderInfo(id);
		return Result.ok(order);
	}


	@ApiOperation(value = "取消预约")
	@GetMapping("auth/cancelOrder/{orderId}")
	public Result cancelOrder(
			@ApiParam(name = "orderId", value = "订单id", required = true)
			@PathVariable("orderId") Long orderId) {
		return Result.ok(orderService.cancelOrder(orderId));
	}

	@ApiOperation(value = "确认取号")
	@GetMapping("inner/confirmGetNumber/{hoscode}/{hosRecordId}")
	public Boolean confirmGetNumber(
			@ApiParam(name = "hoscode", value = "医院code", required = true)
			@PathVariable("hoscode") String hoscode,

			@ApiParam(name = "hosRecordId", value = "预约记录唯一标识（医院预约记录主键）", required = true)
			@PathVariable("hosRecordId") String hosRecordId) {
		return orderService.confirmGetNumber(hoscode, hosRecordId);
	}

	@ApiOperation(value = "获取订单统计数据")
	@PostMapping("inner/getCountMap")
	public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
		return orderService.getCountMap(orderCountQueryVo);
	}
}

