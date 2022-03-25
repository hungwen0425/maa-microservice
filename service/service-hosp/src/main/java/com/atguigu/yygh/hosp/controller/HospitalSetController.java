package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.cms.Banner;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author qy
 *
 */
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

	@Autowired
	private HospitalSetService hospitalSetService;

	@ApiOperation(value = "获取分页列表")
	@GetMapping("{page}/{limit}")
	public Result index(
			@ApiParam(name = "page", value = "当前页码", required = true)
			@PathVariable Integer page,

			@ApiParam(name = "limit", value = "每页记录数", required = true)
			@PathVariable Integer limit,

			@ApiParam(name = "hospitalSetQueryVo", value = "查询对象", required = false)
			HospitalSetQueryVo hospitalSetQueryVo) {
		Page<HospitalSet> pageParam = new Page<>(page, limit);
		IPage<HospitalSet> pageModel = hospitalSetService.selectPage(pageParam, hospitalSetQueryVo);
		return Result.ok(pageModel);
	}

	@ApiOperation(value = "获取医院设置")
	@GetMapping("get/{id}")
	public Result get(
			@ApiParam(name = "id", value = "医院设置id", required = true)
			@PathVariable String id) {
		return Result.ok(hospitalSetService.getById(id));
	}

	@ApiOperation(value = "新增医院设置")
	@PostMapping("save")
	public Result save(@RequestBody HospitalSet hospitalSet) {
		hospitalSetService.save(hospitalSet);
		return Result.ok();
	}

	@ApiOperation(value = "修改医院设置")
	@PutMapping("update")
	public Result updateById(@RequestBody HospitalSet hospitalSet) {
		hospitalSetService.updateById(hospitalSet);
		return Result.ok();
	}

	@ApiOperation(value = "删除医院设置")
	@DeleteMapping("remove/{id}")
	public Result remove(
			@ApiParam(name = "id", value = "医院设置id", required = true)
			@PathVariable String id) {
		hospitalSetService.removeById(id);
		return Result.ok();
	}

	@ApiOperation(value="根据id列表删除医院设置")
	@DeleteMapping("batchRemove")
	public Result batchRemove(@RequestBody List<String> idList){
		hospitalSetService.removeByIds(idList);
		return Result.ok();
	}

	@ApiOperation(value = "锁定")
	@GetMapping("lock/{id}/{status}")
	public Result lock(
			@ApiParam(name = "id", value = "医院设置id", required = true)
			@PathVariable("id") String id,

			@ApiParam(name = "status", value = "锁定状态（0：锁定 1：解锁）", required = true)
			@PathVariable("status") Integer status){
		hospitalSetService.lock(id, status);
		return Result.ok();
	}

	@ApiOperation(value = "发送SignKey")
	@GetMapping("sendSignKey/{id}")
	public Result sendSignKey(
			@ApiParam(name = "id", value = "医院设置id", required = true)
			@PathVariable("id") String id){
		hospitalSetService.sendSignKey(id);
		return Result.ok();
	}
}

