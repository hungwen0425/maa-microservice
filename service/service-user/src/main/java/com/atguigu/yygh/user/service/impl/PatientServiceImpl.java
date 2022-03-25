package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

	@Autowired
	private PatientMapper patientMapper;

	@Autowired
	private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findByUserId(Long userId) {
		List<Patient> patientList = patientMapper.selectList(new QueryWrapper<Patient>().eq("user_id", userId));
		patientList.stream().forEach(item -> {
			this.packPatient(item);
		});
        return patientList;
    }

	@Override
	public  Patient getById(Serializable id) {
		return this.packPatient(patientMapper.selectById(id));
	}

	private Patient packPatient(Patient patient) {
		String certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getCertificatesType());
		String contactsCertificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
		String provinceString = dictFeignClient.getName(patient.getProvinceCode());
		String cityString = dictFeignClient.getName(patient.getCityCode());
		String districtString = dictFeignClient.getName(patient.getDistrictCode());

		patient.getParam().put("certificatesTypeString", certificatesTypeString);
		patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
		patient.getParam().put("provinceString", provinceString);
		patient.getParam().put("cityString", cityString);
		patient.getParam().put("districtString", districtString);
		patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
		return patient;
	}
}
