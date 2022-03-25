package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {

	/**
	 * 上传医院信息
	 * @param paramMap
	 */
	void save(Map<String, Object> paramMap);

	/**
	 * 查询医院
	 * @param hoscode
	 * @return
	 */
	Hospital getByHoscode(String hoscode);

	/**
	 * 分页查询
	 * @param page 当前页码
	 * @param limit 每页记录数
	 * @param hospitalQueryVo 查询条件
	 * @return
	 */
	Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

	/**
	 * 更新上线状态
	 * @param id
	 * @param status
	 */
	void updateStatus(String id, Integer status);

	/**
	 * 医院详情
	 * @param id
	 * @return
	 */
	Map<String, Object> show(String id);

	/**
	 * 根据医院编号获取医院名称接口
	 * @param hoscode
	 * @return
	 */
	String getName(String hoscode);

	/**
	 * 根据医院名称获取医院列表
	 * @param hosname
	 * @return
	 */
	List<Hospital> findByHosname(String hosname);

	/**
	 * 医院预约挂号详情
	 * @param hoscode
	 * @return
	 */
	Map<String, Object> item(String hoscode);
//
//	/**
//	 * 根据id获取对象
//	 * @param id
//	 * @return
//	 */
//	Hospital getById(String id);
//
//	/**
//	 * 医院详情
//	 * @param hoscode
//	 * @return
//	 */
//	Map<String, Object> item(String hoscode);

//
//
//
//	/**
//	 * 根据id删除对象
//	 * @param id
//	 */
//	void removeById(String id);
//
//	/**
//	 *
//	 * @param hoscode
//	 * @return
//	 */
//	Hospital getByHoscode(String hoscode);
//
//	/**
//	 * 获取预约规则
//	 * @param hoscode
//	 * @return
//	 */
//	BookingRule getBookingRuleByHoscode(String hoscode);
//
//	/**
//	 * 获取签名key
//	 * @param hoscode
//	 * @return
//	 */
//	String getSignKey(String hoscode);
//
//	/**
//	 * 获取医院签名信息
//	 * @param hoscode
//	 * @return
//	 */
//	SignInfoVo getSignInfoVo(String hoscode);
}
