package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件接口
 */
public interface FileService {

    /**
     * oss文件上传
     * @param file
     * @param fileHost 文件上传的具体路径
     * @return
     */
    String upload(MultipartFile file, String fileHost) throws IOException;

    /**
     * 根据路径删除文件
     * @param url
     */
    void removeFile(String url);
}
