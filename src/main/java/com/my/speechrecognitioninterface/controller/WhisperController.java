package com.my.speechrecognitioninterface.controller;

import cn.hutool.core.io.FileUtil;
import com.my.speechrecognitioninterface.service.WhisperService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * ��Ƶת����
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class WhisperController {
    @Autowired
    HttpServletRequest req;
    @Value("${file-save-path}")
    private String fileSavePath;
    @Autowired
    private WhisperService whisperService;

    @PostMapping("/uploadToTranscription")
    public ResponseEntity<String> uploadAudioFile(@RequestParam("file") MultipartFile uploadFile) {
        // �����ﴦ���ϴ���Ƶ�ļ����߼�
        String dateTime = new DateTime().toString("yyyy-MM-dd");
        File folder = new File(fileSavePath + dateTime);
        if (folder.isDirectory() && !folder.exists()) {
            boolean mkdirFlag = folder.mkdirs();
        }
        String fileName = uploadFile.getOriginalFilename();
        File localFile = new File(folder, fileName);
        //uploadFile.transferTo(file);//jetty������������ �������������
        try {
            FileUtil.writeBytes(uploadFile.getBytes(), localFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // ����Whisper�������������Ļ�ļ�
        return whisperService.transcription(localFile);
    }
}
