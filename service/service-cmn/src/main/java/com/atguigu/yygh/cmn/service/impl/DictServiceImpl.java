package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.util.BeanUtils;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.common.excel.ExcelHelper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

	@Autowired
	private DictMapper dictMapper;

	/**
	 * 根据上级id获取子节点数据列表
	 * allEntries = true: 方法调用后清空所有缓存
	 * beforeInvocation = true：方法调用前清空所有缓存
	 * @param parentId
	 */
	@Cacheable(value = "dict",keyGenerator = "keyGenerator")
	@Override
	public List<Dict> findByParentId(Long parentId) {
        List<Dict> dictList = dictMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", parentId));
        dictList.stream().forEach(dict -> {
            boolean isHasChildren = this.isHasChildren(dict.getId());
            dict.setHasChildren(isHasChildren);
        });
        return dictList;
	}

	/**
	 * 导入
	 * allEntries = true: 方法调用后清空所有缓存
	 * beforeInvocation = true：方法调用前清空所有缓存
	 * @param file
	 */
	@CacheEvict(value = "dict", allEntries=true)
	@Override
	public void importData(MultipartFile file) {
		ExcelHelper fileHelper = new ExcelHelper(DictEeVo.class);
		List<DictEeVo> dictVoList = fileHelper.importExcel(file);
		if(!CollectionUtils.isEmpty(dictVoList)) {
			dictMapper.insertBatch(dictVoList);
		}
	}

	@Override
	public void exportData(HttpServletResponse response) {
		List<Dict> dictList = dictMapper.selectList(null);
		if(!CollectionUtils.isEmpty(dictList)) {
			List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
			for(Dict dict : dictList) {
				DictEeVo dictVo = new DictEeVo();
				BeanUtils.copyBean(dict, dictVo, DictEeVo.class);
				dictVoList.add(dictVo);
			}
			ExcelHelper fileHelper = new ExcelHelper(DictEeVo.class);
			fileHelper.exporExcel(dictVoList, "数据字典", response);
		}
	}

	@Cacheable(value = "dict",keyGenerator = "keyGenerator")
	@Override
	public List<Dict> findByDictCode(String dictCode) {
		Dict dict = this.getByDictsCode(dictCode);
		if(null == dict) return null;
		return this.findByParentId(dict.getId());
	}

	@Cacheable(value = "dict",keyGenerator = "keyGenerator")
	public Dict getByDictsCode(String dictCode) {
		return dictMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
	}

	@Cacheable(value = "dict",keyGenerator = "keyGenerator")
	@Override
	public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
		//如果value能唯一定位数据字典，parentDictCode可以传空，例如：省市区的value值能够唯一确定
		if(StringUtils.isEmpty(parentDictCode)) {
			Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
			if(null != dict) {
				return dict.getName();
			}
		} else {
			Dict parentDict = this.getByDictsCode(parentDictCode);
			if(null == parentDict) return "";
			Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentDict.getId()).eq("value", value));
			if(null != dict) {
				return dict.getName();
			}
		}
		return "";
	}

//	/**
//	 * https://alibaba-easyexcel.github.io/quickstart/read.html
//	 * @param file
//	 */
//	@Override
//	public void importData(MultipartFile file) {
//		try {
//			EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictDataListener(dictMapper)).sheet().doRead();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * https://alibaba-easyexcel.github.io/quickstart/write.html
//	 * @param response
//	 */
//	@Override
//	public void exportData(HttpServletResponse response) {
//		try {
//			response.setContentType("application/vnd.ms-excel");
//			response.setCharacterEncoding("utf-8");
//			// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//			String fileName = URLEncoder.encode("数据字典", "UTF-8");
//			response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
//
//			List<Dict> dictList = dictMapper.selectList(null);
//			List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
//			for(Dict dict : dictList) {
//				DictEeVo dictVo = new DictEeVo();
//				BeanUtils.copyBean(dict, dictVo, DictEeVo.class);
//				dictVoList.add(dictVo);
//			}
//
//			EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 判断该节点是否有子节点
	 * @param id
	 * @return
	 */
	private boolean isHasChildren(Long id) {
		Integer count = dictMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id));
		if(count.intValue() > 0) {
			return true;
		}
		return false;
	}

}
