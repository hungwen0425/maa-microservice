package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.MmaException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.util.BeanUtils;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private HospitalService hospitalService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DepartmentService departmentService;


	@Override
	public void save(Map<String, Object> paramMap) {
		Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
		Schedule targetSchedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
		if(null != targetSchedule) {
			//值copy不为null的值，该方法为自定义方法
			BeanUtils.copyBean(schedule, targetSchedule, Schedule.class);
			scheduleRepository.save(targetSchedule);
		} else {
			schedule.setCreateTime(new Date());
			schedule.setUpdateTime(new Date());
			schedule.setIsDeleted(0);
			scheduleRepository.save(schedule);
		}
	}

	@Override
	public Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {

		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		//0为第一页
		Pageable pageable = PageRequest.of(page-1, limit, sort);

		Schedule schedule = new Schedule();
		BeanUtils.copyProperties(scheduleQueryVo, schedule);
		schedule.setIsDeleted(0);

		//创建匹配器，即如何使用查询条件
		ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
				.withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

		//创建实例
		Example<Schedule> example = Example.of(schedule, matcher);
		Page<Schedule> pages = scheduleRepository.findAll(example, pageable);
		return pages;
	}

	@Override
	public void remove(String hoscode, String hosScheduleId) {
		Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
		if(null != schedule) {
			scheduleRepository.deleteById(schedule.getId());
		}
	}

	@Override
	public Map<String, Object> getScheduleRule(int page, int limit, String hoscode, String depcode) {
		Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.group("workDate")//分组字段
						.first("workDate").as("workDate")
						.count().as("docCount")
						.sum("availableNumber").as("availableNumber")
						.sum("reservedNumber").as("reservedNumber"),
				Aggregation.sort(Sort.by(Sort.Direction.DESC, "workDate")),  // 排序
				Aggregation.skip((page-1)*limit),
				Aggregation.limit(limit)
		);
		AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
		List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults();

		// 分组查询分组后的总记录数
		Aggregation totalAgg = Aggregation.newAggregation(
				Aggregation.match(criteria),     //查询条件
				Aggregation.group("workDate")      //分组条件
		);
		AggregationResults<BookingScheduleRuleVo> totalAggregationResults = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
		int total = totalAggregationResults.getMappedResults().size();

		//获取可预约排班规则
		//List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
		for(BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
			//计算当前预约日期为周几
			String dayOfWeek = this.getDayOfWeek(new DateTime(bookingScheduleRuleVo.getWorkDate()));
			bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("bookingScheduleList", bookingScheduleRuleVoList);
		result.put("total", total);

		//其他基础数据
		Map<String, String> baseMap = new HashMap<>();
		//医院名称
		baseMap.put("hosname", hospitalService.getName(hoscode));
		result.put("baseMap", baseMap);
		return result;
	}

	/**
	 * 根据日期获取周几数据
	 * @param dateTime
	 * @return
	 */
	private String getDayOfWeek(DateTime dateTime) {
		String dayOfWeek = "";
		switch (dateTime.getDayOfWeek()) {
			case DateTimeConstants.SUNDAY:
				dayOfWeek = "周日";
				break;
			case DateTimeConstants.MONDAY:
				dayOfWeek = "周一";
				break;
			case DateTimeConstants.TUESDAY:
				dayOfWeek = "周二";
				break;
			case DateTimeConstants.WEDNESDAY:
				dayOfWeek = "周三";
				break;
			case DateTimeConstants.THURSDAY:
				dayOfWeek = "周四";
				break;
			case DateTimeConstants.FRIDAY:
				dayOfWeek = "周五";
				break;
			case DateTimeConstants.SATURDAY:
				dayOfWeek = "周六";
			default:
				break;
		}
		return dayOfWeek;
	}

	@Override
	public List<Schedule> findScheduleList(String hoscode, String depcode, String workDate) {
		//排班信息
		List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode,  new DateTime(workDate).toDate());
		scheduleList.stream().forEach(item -> {
			this.packSchedule(item);
		});
		// 升序排列
		Collections.sort(scheduleList, new Comparator<Schedule>() {
			public int compare(Schedule a1, Schedule a2) {
				return a1.getWorkTime() - a2.getWorkTime();
			}
		});
		//排班信息
		return scheduleList;
	}

	private Schedule packSchedule(Schedule schedule) {
		schedule.getParam().put("hosname", hospitalService.getName(schedule.getHoscode()));
		schedule.getParam().put("depname", departmentService.getName(schedule.getHoscode(), schedule.getDepcode()));
		schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
		return schedule;
	}

	@Override
	public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {
		Map<String, Object> result = new HashMap<>();

		//获取预约规则
		Hospital hospital = hospitalService.getByHoscode(hoscode);
		if(null == hospital) {
			throw new MmaException(ResultCodeEnum.DATA_ERROR);
		}
		BookingRule bookingRule = hospital.getBookingRule();

		//获取可预约日期分页数据
		IPage iPage = this.getListDate(page, limit, bookingRule);
		//当前页可预约日期
		List<Date> dateList = iPage.getRecords();
		//获取可预约日期科室剩余预约数
		Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.group("workDate")//分组字段
						.first("workDate").as("workDate")
						.count().as("docCount")
						.sum("availableNumber").as("availableNumber")
						.sum("reservedNumber").as("reservedNumber")
		);
		AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
		List<BookingScheduleRuleVo> scheduleVoList = aggregationResults.getMappedResults();
		//获取科室剩余预约数

		//合并数据 将统计数据ScheduleVo根据“安排日期”合并到BookingRuleVo
		Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(scheduleVoList)) {
			scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
		}
		//获取可预约排班规则
		List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
		for(int i=0, len=dateList.size(); i<len; i++) {
			Date date = dateList.get(i);

			BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
			if(null == bookingScheduleRuleVo) { // 说明当天没有排班医生
				bookingScheduleRuleVo = new BookingScheduleRuleVo();
				//就诊医生人数
				bookingScheduleRuleVo.setDocCount(0);
				//科室剩余预约数  -1表示无号
				bookingScheduleRuleVo.setAvailableNumber(-1);
			}
			bookingScheduleRuleVo.setWorkDate(date);
			bookingScheduleRuleVo.setWorkDateMd(date);
			//计算当前预约日期为周几
			String dayOfWeek = this.getDayOfWeek(new DateTime(date));
			bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

			//最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
			if(i == len-1 && page == iPage.getPages()) {
				bookingScheduleRuleVo.setStatus(1);
			} else {
				bookingScheduleRuleVo.setStatus(0);
			}
			//当天预约如果过了停号时间， 不能预约
			if(i == 0 && page == 1) {
				DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
				if(stopTime.isBeforeNow()) {
					//停止预约
					bookingScheduleRuleVo.setStatus(-1);
				}
			}
			bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
		}

		//可预约日期规则数据
		result.put("bookingScheduleList", bookingScheduleRuleVoList);
		result.put("total", iPage.getTotal());

		//其他基础数据
		Map<String, String> baseMap = new HashMap<>();
		//医院名称
		baseMap.put("hosname", hospitalService.getName(hoscode));
		//科室
		Department department =departmentService.getDepartment(hoscode, depcode);
		//大科室名称
		baseMap.put("bigname", department.getBigname());
		//科室名称
		baseMap.put("depname", department.getDepname());
		//月
		baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
		//放号时间
		baseMap.put("releaseTime", bookingRule.getReleaseTime());
		//停号时间
		baseMap.put("stopTime", bookingRule.getStopTime());
		result.put("baseMap", baseMap);
		return result;
	}

	/**
	 * 获取可预约日期分页数据
	 * @param page
	 * @param limit
	 * @param bookingRule
	 * @return
	 */
	private IPage<Date> getListDate(int page, int limit, BookingRule bookingRule) {
		//当天放号时间
		DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
		//预约周期
		int cycle = bookingRule.getCycle();
		//如果当天放号时间已过，则预约周期后一天为即将放号时间，周期加1
		if(releaseTime.isBeforeNow()) cycle += 1;
		//可预约所有日期，最后一天显示即将放号倒计时
		List<Date> dateList = new ArrayList<>();
		for (int i = 0; i < cycle; i++) {
			//计算当前预约日期
			DateTime curDateTime = new DateTime().plusDays(i);
			String dateString = curDateTime.toString("yyyy-MM-dd");
			dateList.add(new DateTime(dateString).toDate());
		}

		//日期分页，由于预约周期不一样，页面一排最多显示7天数据，多了就要分页显示
		List<Date> pageDateList = new ArrayList<>();
		int start = (page-1)*limit;
		int end = (page-1)*limit+limit;
		if(end >dateList.size()) end = dateList.size();
		for (int i = start; i < end; i++) {
			pageDateList.add(dateList.get(i));
		}
		IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(page, 7, dateList.size());
		iPage.setRecords(pageDateList);
		return iPage;
	}

	/**
	 * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
	 * @param date
	 * @param timeString
	 * @return
	 */
	private DateTime getDateTime(Date date, String timeString) {
		String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
		DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
		return dateTime;
	}

	@Override
	public Schedule getById(String id) {
		Schedule schedule = scheduleRepository.findById(id).get();
		return this.packSchedule(schedule);
	}

	@Override
	public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
		ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

		//排班信息
		Schedule schedule = this.getById(scheduleId);
		if(null == schedule) {
			throw new MmaException(ResultCodeEnum.PARAM_ERROR);
		}

		//获取预约规则信息
		Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
		if(null == hospital) {
			throw new MmaException(ResultCodeEnum.DATA_ERROR);
		}
		BookingRule bookingRule = hospital.getBookingRule();
		if(null == bookingRule) {
			throw new MmaException(ResultCodeEnum.PARAM_ERROR);
		}

		scheduleOrderVo.setHoscode(schedule.getHoscode());
		scheduleOrderVo.setHosname(hospitalService.getName(schedule.getHoscode()));
		scheduleOrderVo.setDepcode(schedule.getDepcode());
		scheduleOrderVo.setDepname(departmentService.getName(schedule.getHoscode(), schedule.getDepcode()));
		scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
		scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
		scheduleOrderVo.setTitle(schedule.getTitle());
		scheduleOrderVo.setReserveDate(schedule.getWorkDate());
		scheduleOrderVo.setReserveTime(schedule.getWorkTime());
		scheduleOrderVo.setAmount(schedule.getAmount());

		//退号截止天数（如：就诊前一天为-1，当天为0）
		int quitDay = bookingRule.getQuitDay();
		DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
		scheduleOrderVo.setQuitTime(quitTime.toDate());

		//预约开始时间
		DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
		scheduleOrderVo.setStartTime(startTime.toDate());

		//预约截止时间
		DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
		scheduleOrderVo.setEndTime(endTime.toDate());

		//当天停止挂号时间
		DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
		scheduleOrderVo.setStartTime(startTime.toDate());
		return scheduleOrderVo;
	}

	@Override
	public void update(Schedule schedule) {
		schedule.setUpdateTime(new Date());
		//主键一致就是更新
		scheduleRepository.save(schedule);
	}
}
