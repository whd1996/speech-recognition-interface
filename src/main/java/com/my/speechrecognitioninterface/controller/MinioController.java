package com.my.speechrecognitioninterface.controller;


import com.my.speechrecognitioninterface.service.WhisperService;
import com.my.speechrecognitioninterface.utils.MinioUtilS;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@Api(tags = "minio文件管理")
@Slf4j
@RequestMapping("/minio")
public class MinioController {

    @Autowired
    private MinioUtilS minioUtilS;
    @Autowired
    HttpServletResponse resp;
    @Value("${minio.endpoint}")
    private String address;
    @Value("${minio.bucketName}")
    private String bucketName;
    @Autowired
    private WhisperService whisperService;
    @PostMapping("/upload")
    @ApiImplicitParam(name = "file", value = "文件上传", required = true, dataTypeClass = MultipartFile.class, allowMultiple = true, paramType = "query")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        minioUtilS.existBucket(bucketName);
        List<String> upload = minioUtilS.upload(new MultipartFile[]{file});
        new Thread(()->{
            try {
                File tempFile = File.createTempFile("temp", ".tmp");
                file.transferTo(tempFile);
                whisperService.transcription(tempFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        String url = address + "/" + bucketName + "/" + upload.get(0);
        return  ResponseEntity.ok(url);
    }


}

