package com.atguigu.yygh.cmn.service;


import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;;import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface DictService extends IService<Dict> {

    /**
     * 根据上级id获取子节点数据列表
     * @param parentId
     * @return
     */
    List<Dict> findByParentId(Long parentId);

    /**
     * 导入
     * @param file
     */
    void importData(MultipartFile file);

    /**
     * 导出
     * @param response
     */
    void exportData(HttpServletResponse response);

    /**
     * 根据编码获取数据字典列表
     * @param dictCode
     * @return
     */
    List<Dict> findByDictCode(String dictCode);

    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode
     * @param value
     * @return
     */
    String getNameByParentDictCodeAndValue(String parentDictCode, String value);

}
