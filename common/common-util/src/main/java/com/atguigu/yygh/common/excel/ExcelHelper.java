package com.atguigu.yygh.common.excel;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper<T> {

    private Class<T>  clazz;;

    public ExcelHelper(Class<T>  clazz){
        this.clazz = clazz;
    }

    public List<T> importExcel(MultipartFile file) {
        List<T> list = new ArrayList<>();
        try {
            //实例化实现了AnalysisEventListener接口的类
            ExcelListener listener = new ExcelListener();

            EasyExcel.read(file.getInputStream(), clazz, listener).sheet().doRead();
            //获取数据
            list = listener.getDatas();
        } catch (IOException e) {
            e.printStackTrace();
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        return list;
    }

    public void exporExcel(List<T> list, String fileName, HttpServletResponse response) {
        try {
            //添加响应头信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String curFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + curFileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), clazz).sheet(curFileName).doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
    }
}
