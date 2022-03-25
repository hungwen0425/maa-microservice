package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author qy
 *
 */
@Api(tags = "排班管理")
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@ApiOperation(value = "获取排班规则数据")
	@GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
	public Result getSchedule(
			@ApiParam(name = "page", value = "当前页码", required = true)
			@PathVariable Integer page,

			@ApiParam(name = "limit", value = "每页记录数", required = true)
			@PathVariable Integer limit,

			@ApiParam(name = "hoscode", value = "医院code", required = true)
			@PathVariable String hoscode,

			@ApiParam(name = "depcode", value = "科室code", required = true)
			@PathVariable String depcode) {
		return Result.ok(scheduleService.getScheduleRule(page, limit, hoscode, depcode));
	}

	@ApiOperation(value = "获取workDate排班日期的排班详细列表")
	@GetMapping("findScheduleList/{hoscode}/{depcode}/{workDate}")
	public Result findScheduleList(
			@ApiParam(name = "hoscode", value = "医院code", required = true)
			@PathVariable String hoscode,

			@ApiParam(name = "depcode", value = "科室code", required = true)
			@PathVariable String depcode,

			@ApiParam(name = "workDate", value = "排班日期", required = true)
			@PathVariable String workDate) {
		return Result.ok(scheduleService.findScheduleList(hoscode, depcode, workDate));
	}

}

