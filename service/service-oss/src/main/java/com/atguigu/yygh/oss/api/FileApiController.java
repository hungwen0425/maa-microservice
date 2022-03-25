package com.atguigu.yygh.oss.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = "文件上传")
@RestController
@RequestMapping("/api/oss/file")
@Slf4j
public class FileApiController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "文件上传")
    @PostMapping("fileUpload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("fileHost") String fileHost) throws IOException {
        String uploadUrl = fileService.upload(file, fileHost);
        return Result.ok(uploadUrl);
    }

}
