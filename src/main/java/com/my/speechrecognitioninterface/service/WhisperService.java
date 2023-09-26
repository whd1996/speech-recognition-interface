package com.my.speechrecognitioninterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * description:
 *
 * @author whd
 * @version 1.0.0
 * @date 2023/09/26 11:45:36
 */
@Service
@Slf4j
public class WhisperService {
    @Value("${whisper.model}")
    private String model;
    @Value("${whisper.language}")
    private String language;
    @Value("${whisper.output-dir}")
    private String outputDir;
    @Value("${whisper.output_format}")
    private String outputFormat;

    public ResponseEntity<String> transcription(File localFile) {
        String inputPath = localFile.toString().replace("\\", "\\\\");
        log.info("�ļ�·����localFile=>{}", inputPath);
        log.info("�ļ�����·����localFile=>{}", localFile.getAbsolutePath());
        log.info("���·����outputPath=>{}", outputDir);
        StringBuilder transcription = new StringBuilder();
        transcription.append("�������:\n");
        try {
            Process process = getProcess(inputPath);
            // ����Whisper�����
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                transcription.append(line).append("\n");
            }
            // �ȴ�Whisper�������
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // ת¼�ɹ���transcription����������Ļ
                log.info("ת¼�ɹ���");
            } else {
                // ת¼ʧ��
                return ResponseEntity.ok("ת¼ʧ�ܣ�");
            }
        } catch (IOException e) {
           log.error(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // ����Whisper�������������Ļ�ļ�
        return ResponseEntity.ok(transcription.toString());
    }

    private Process getProcess(String inputPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("whisper", inputPath,
                "--output_format",outputFormat ,
                "--output_dir", outputDir,
                "--model", model,
                "--language", language)
                .inheritIO();
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
