package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

;

public interface ScheduleService {

	/**
	 * 上传排班
	 * @param paramMap
	 */
	void save(Map<String, Object> paramMap);

	/**
	 * 分页查询
	 * @param page 当前页码
	 * @param limit 每页记录数
	 * @param scheduleQueryVo 查询条件
	 * @return
	 */
	Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

	/**
	 * 删除科室
	 * @param hoscode
	 * @param hosScheduleId
	 */
	void remove(String hoscode, String hosScheduleId);

	/**
	 * 获取排班日期数据
	 * @param page
	 * @param limit
	 * @param hoscode
	 * @param depcode
	 * @return
	 */
	Map<String, Object> getScheduleRule(int page, int limit, String hoscode, String depcode);


	/**
	 * 获取workDate排班日期的排班详细列表
	 * @param hoscode
	 * @param depcode
	 * @param workDate
	 * @return
	 */
	List<Schedule> findScheduleList(String hoscode, String depcode, String workDate);

	/**
	 * 获取排班可预约日期数据
	 * @param page
	 * @param limit
	 * @param hoscode
	 * @param depcode
	 * @return
	 */
	Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

	/**
	 * 根据id获取排班
	 * @param id
	 * @return
	 */
	Schedule getById(String id);

	/**
	 * 根据排班id获取预约下单数据
	 * @param scheduleId
	 * @return
	 */
	ScheduleOrderVo getScheduleOrderVo(String scheduleId);

	/**
	 * 修改排班
	 * @param schedule
	 */
	void update(Schedule schedule);
}
