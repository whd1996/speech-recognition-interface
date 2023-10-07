package com.my.speechrecognitioninterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        StringBuilder result = new StringBuilder();
        try {
            String inputPath = localFile.toString().replace("\\", "\\\\");
            log.info("�ļ�·����localFile=>{}", inputPath);
            log.info("�ļ�����·����localFile=>{}", localFile.getAbsolutePath());
            log.info("���·����outputPath=>{}", outputDir);
            long start = System.currentTimeMillis();
            Process process = getProcess(inputPath);
            // ����Whisper�����
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                //תutf-8���
                String str = new String(line.getBytes(), StandardCharsets.UTF_8);
                System.out.println(str);
            }
            // �ȴ�Whisper�������
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // ת¼�ɹ���transcription����������Ļ
                long time = System.currentTimeMillis() - start;
                System.out.println("��ʱ: " + time + "ms");
                log.info("ת¼�ɹ���");
                String[] split = localFile.getName().split("\\.");
                reader = new BufferedReader(new FileReader(outputDir+split[0]+"."+outputFormat));
               /* result.append("�������:\n");*/
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                System.out.println(result);
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
        return ResponseEntity.ok(result.toString());
    }

    private Process getProcess(String inputPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("whisper", inputPath,
                "--output_format", outputFormat,
                "--output_dir", outputDir,
                "--model", model,
                "--language", language)
                .inheritIO();
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
