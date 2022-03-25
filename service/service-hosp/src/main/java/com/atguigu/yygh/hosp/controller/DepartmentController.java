package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
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
@Api(tags = "科室管理接口")
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@ApiOperation(value = "获取分页列表")
	@GetMapping("{hoscode}")
	public Result index(
			@ApiParam(name = "hoscode", value = "医院code", required = true)
			@PathVariable String hoscode) {
		return Result.ok(departmentService.findTree(hoscode));
	}
}

