package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

;import java.util.List;

public interface PatientService extends IService<Patient> {

	/**
	 *
	 * @param userId
	 * @return
	 */
	List<Patient> findByUserId(Long userId);
}
