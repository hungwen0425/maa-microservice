package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.util.BeanUtils;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {

	@Autowired
	private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

	@Autowired
	private DepartmentService departmentService;

	@Override
	public void save(Map<String, Object> paramMap) {
		log.info(JSONObject.toJSONString(paramMap));
		Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap),Hospital.class);
		//判断是否存在
		Hospital targetHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
		if(null != targetHospital) {
			hospital.setStatus(targetHospital.getStatus());
			hospital.setCreateTime(targetHospital.getCreateTime());
			hospital.setUpdateTime(new Date());
			hospital.setIsDeleted(0);
			hospitalRepository.save(hospital);
		} else {
			//0：未上线 1：已上线
			hospital.setStatus(0);
			hospital.setCreateTime(new Date());
			hospital.setUpdateTime(new Date());
			hospital.setIsDeleted(0);
			hospitalRepository.save(hospital);
		}
	}

	@Override
	public Hospital getByHoscode(String hoscode) {
		return hospitalRepository.getHospitalByHoscode(hoscode);
	}

	@Override
	public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		//0为第一页
		Pageable pageable = PageRequest.of(page-1, limit, sort);

		Hospital hospital = new Hospital();
		BeanUtils.copyProperties(hospitalQueryVo, hospital);

		//创建匹配器，即如何使用查询条件
		ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
				.withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

		//创建实例
		Example<Hospital> example = Example.of(hospital, matcher);
		Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

		pages.getContent().stream().forEach(item -> {
			this.packHospital(item);
		});
		return pages;
	}

    /**
     * 封装数据
     * @param hospital
     * @return
     */
    private Hospital packHospital(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(),hospital.getHostype());
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }

	@Override
	public void updateStatus(String id, Integer status) {
		if(status.intValue() == 0 || status.intValue() == 1) {
			Hospital hospital = this.getById(id);
			hospital.setStatus(status);
			hospital.setUpdateTime(new Date());
			hospitalRepository.save(hospital);
		}
	}

	/**
	 * 根据id获取对象
	 * @param id
	 * @return
	 */
	private Hospital getById(String id) {
		return hospitalRepository.findById(id).get();
	}

    @Override
    public Map<String, Object> show(String id) {
        Map<String, Object> result = new HashMap<>();

        Hospital hospital = this.packHospital(this.getById(id));
        result.put("hospital", hospital);

        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

	@Override
	public String getName(String hoscode) {
		Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
		if(null != hospital) {
			return hospital.getHosname();
		}
		return "";
	}

	@Override
	public List<Hospital> findByHosname(String hosname) {
		return hospitalRepository.findHospitalByHosnameLike(hosname);
	}

	@Override
	public Map<String, Object> item(String hoscode) {
		Map<String, Object> result = new HashMap<>();

		//医院详情
		Hospital hospital = this.packHospital(this.getByHoscode(hoscode));
		result.put("hospital", hospital);

		//预约规则
		result.put("bookingRule", hospital.getBookingRule());
		//不需要重复返回
		hospital.setBookingRule(null);
		return result;
	}
}
