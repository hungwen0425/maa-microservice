package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.util.BeanUtils;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Override
	public void save(Map<String, Object> paramMap) {
		Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);
		Department targetDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
		if(null != targetDepartment) {
			//copy不为null的值，该方法为自定义方法
			BeanUtils.copyBean(department, targetDepartment, Department.class);
			departmentRepository.save(targetDepartment);
		} else {
			department.setCreateTime(new Date());
			department.setUpdateTime(new Date());
			department.setIsDeleted(0);
			departmentRepository.save(department);
		}
	}

	@Override
	public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		//0为第一页
		Pageable pageable = PageRequest.of(page-1, limit, sort);

		Department department = new Department();
		BeanUtils.copyProperties(departmentQueryVo, department);
		department.setIsDeleted(0);

		//创建匹配器，即如何使用查询条件
		ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
				.withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

		//创建实例
		Example<Department> example = Example.of(department, matcher);
		Page<Department> pages = departmentRepository.findAll(example, pageable);
		return pages;
	}

	@Override
	public void remove(String hoscode, String depcode) {
		Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
		if(null != department) {
			//departmentRepository.delete(department);
			departmentRepository.deleteById(department.getId());
		}
	}

	@Override
	public List<DepartmentVo> findTree(String hoscode) {
		List<DepartmentVo> result = new ArrayList<>();

		//根据医院code获取医院科室
		Department departmentQuery = new Department();
		departmentQuery.setHoscode(hoscode);
		Example example = Example.of(departmentQuery);
		Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
		List<Department> departmentList = departmentRepository.findAll(example, sort);

		//根据大科室code分组，获取大科室code与下级科室列表
		Map<String, List<Department>> department1Map = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
		for(Map.Entry<String, List<Department>> entry : department1Map.entrySet()){
			//大科室code
			String bigcode = entry.getKey();
			//大科室code对应的全部数据（二级数据）
			List<Department> department1List = entry.getValue();

			//大科室
			DepartmentVo departmentVo1 = new DepartmentVo();
			departmentVo1.setDepcode(bigcode);
			departmentVo1.setDepname(department1List.get(0).getBigname());

			//小科室
			List<DepartmentVo> children = new ArrayList<>();
			for(Department department : department1List) {
				DepartmentVo departmentVo2 = new DepartmentVo();
				departmentVo2.setDepcode(department.getDepcode());
				departmentVo2.setDepname(department.getDepname());

				children.add(departmentVo2);
			}
			departmentVo1.setChildren(children);
			result.add(departmentVo1);
		}
		return result;
	}

	@Override
	public String getName(String hoscode, String depcode) {
		Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
		if(null != department) {
			return department.getDepname();
		}
		return "";
	}

	@Override
	public Department getDepartment(String hoscode, String depcode) {
		return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
	}
}
