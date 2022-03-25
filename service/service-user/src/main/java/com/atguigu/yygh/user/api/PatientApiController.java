package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.util.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author qy
 *
 */
@Api(tags = "就诊人接口")
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

	@Autowired
	private PatientService patientService;

	@ApiOperation(value = "获取列表")
	@GetMapping("auth/findAll")
	public Result findAll(HttpServletRequest request) {
		return Result.ok(patientService.findByUserId(AuthContextHolder.getUserId(request)));
	}

	@ApiOperation(value = "获取就诊人")
	@GetMapping("auth/get/{id}")
	public Result get(
			@ApiParam(name = "id", value = "就诊人id", required = true)
			@PathVariable Long id) {
		return Result.ok(patientService.getById(id));
	}

	@ApiOperation(value = "新增就诊人")
	@PostMapping("auth/save")
	public Result save(@RequestBody Patient patient, HttpServletRequest request) {
		patient.setUserId(AuthContextHolder.getUserId(request));
		return Result.ok(patientService.save(patient));
	}

	@ApiOperation(value = "修改就诊人")
	@PutMapping("auth/update")
	public Result updateById(@RequestBody Patient patient) {
		return Result.ok(patientService.updateById(patient));
	}

	@ApiOperation(value = "删除就诊人")
	@DeleteMapping("auth/remove/{id}")
	public Result remove(
			@ApiParam(name = "id", value = "就诊人id", required = true)
			@PathVariable Long id) {
		return Result.ok(patientService.removeById(id));
	}

	@ApiOperation(value = "获取就诊人")
	@GetMapping("inner/get/{id}")
	public Patient getPatient(
			@ApiParam(name = "id", value = "就诊人id", required = true)
			@PathVariable("id") Long id) {
		return patientService.getById(id);
	}
}

