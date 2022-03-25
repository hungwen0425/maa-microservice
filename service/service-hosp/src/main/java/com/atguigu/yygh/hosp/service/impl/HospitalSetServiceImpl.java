package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.common.constant.MqConst;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.service.RabbitService;
import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

	@Autowired
	private HospitalSetMapper hospitalSetMapper;

	@Autowired
	private RabbitService rabbitService;

	@Override
	public IPage<HospitalSet> selectPage(Page<HospitalSet> pageParam, HospitalSetQueryVo hospitalSetQueryVo) {
		return hospitalSetMapper.selectPage(pageParam, hospitalSetQueryVo);
	}

	@Override
	public boolean save(HospitalSet hospitalSet) {
		int count = hospitalSetMapper.selectCount(new QueryWrapper<HospitalSet>().eq("hoscode", hospitalSet.getHoscode()));
		if(count > 0) {
			throw new YyghException(ResultCodeEnum.HOSCODE_EXIST);
		}
		//自定义生成
		Random random = new Random();
		hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
		hospitalSet.setStatus(1);
		hospitalSetMapper.insert(hospitalSet);
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void lock(String id, Integer status) {
		if(status.intValue() == 0 || status.intValue() == 1) {
			HospitalSet hospitalSet = this.getById(id);
			hospitalSet.setStatus(status);
			this.updateById(hospitalSet);
		}
	}

	@Override
	public void sendSignKey(String id) {
		HospitalSet hospitalSet = this.getById(id);
		String hoscode = hospitalSet.getHoscode();
		String signKey = hospitalSet.getSignKey();
		//发送短信，后续补充
		MsmVo msmVo = new MsmVo();
		msmVo.setPhone(hospitalSet.getContactsPhone());
		msmVo.setTemplateCode("SMS_194650696");
		Map<String,Object> param = new HashMap<String,Object>(){{
			put("hoscode", hoscode);
			put("signKey", signKey);
		}};
		msmVo.setParam(param);
		rabbitService.sendMessage(MqConst.QUEUE_MSM_ITEM, MqConst.ROUTING_MSM_ITEM, msmVo);
	}

	@Override
	public String getSignKey(String hoscode) {
		HospitalSet hospitalSet = this.getByHoscode(hoscode);
		if(null == hospitalSet) {
			throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
		}
		if(hospitalSet.getStatus().intValue() == 0) {
			throw new YyghException(ResultCodeEnum.HOSPITAL_LOCK);
		}
		return hospitalSet.getSignKey();
	}

	/**
	 * 根据hoscode获取医院设置
	 * @param hoscode
	 * @return
	 */
	private HospitalSet getByHoscode(String hoscode) {
		return hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>().eq("hoscode", hoscode));
	}

	@Override
	public SignInfoVo getSignInfoVo(String hoscode) {
		HospitalSet hospitalSet = this.getByHoscode(hoscode);
		if(null == hospitalSet) {
			throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
		}
		SignInfoVo signInfoVo = new SignInfoVo();
		signInfoVo.setApiUrl(hospitalSet.getApiUrl());
		signInfoVo.setSignKey(hospitalSet.getSignKey());
		return signInfoVo;
	}
}
