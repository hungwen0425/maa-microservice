package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

;

public interface DepartmentService {

	/**
	 * 上传科室信息
	 * @param paramMap
	 */
	void save(Map<String, Object> paramMap);

	/**
	 * 分页查询
	 * @param page 当前页码
	 * @param limit 每页记录数
	 * @param departmentQueryVo 查询条件
	 * @return
	 */
	Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

	/**
	 * 删除科室
	 * @param hoscode
	 * @param depcode
	 */
	void remove(String hoscode, String depcode);

	/**
	 * 获取科室树形数据
	 * @param hoscode
	 * @return
	 */
	List<DepartmentVo> findTree(String hoscode);

	/**
	 * 根据部门编号获取部门名称
	 * @param hoscode
	 * @param depcode
	 * @return
	 */
	String getName(String hoscode, String depcode);

	/**
	 * 获取部门
	 * @param hoscode
	 * @param depcode
	 * @return
	 */
	Department getDepartment(String hoscode, String depcode);


//	void save(Map<String, Object> paramMap);
//
//	/**
//	 * 根据id删除对象
//	 * @param id
//	 */
//	void removeById(String id);

//	Department getDepartment(String hoscode, String depcode);

}
