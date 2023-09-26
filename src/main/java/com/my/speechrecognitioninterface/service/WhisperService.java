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
        log.info("文件路径：localFile=>{}", inputPath);
        log.info("文件绝对路径：localFile=>{}", localFile.getAbsolutePath());
        log.info("输出路径：outputPath=>{}", outputDir);
        StringBuilder transcription = new StringBuilder();
        transcription.append("解析结果:\n");
        try {
            Process process = getProcess(inputPath);
            // 处理Whisper的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                transcription.append(line).append("\n");
            }
            // 等待Whisper进程完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 转录成功，transcription包含中文字幕
                log.info("转录成功！");
            } else {
                // 转录失败
                return ResponseEntity.ok("转录失败！");
            }
        } catch (IOException e) {
           log.error(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 处理Whisper的输出并返回字幕文件
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
