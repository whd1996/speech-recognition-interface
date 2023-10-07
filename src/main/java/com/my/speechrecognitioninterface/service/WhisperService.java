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
            log.info("文件路径：localFile=>{}", inputPath);
            log.info("文件绝对路径：localFile=>{}", localFile.getAbsolutePath());
            log.info("输出路径：outputPath=>{}", outputDir);
            long start = System.currentTimeMillis();
            Process process = getProcess(inputPath);
            // 处理Whisper的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                //转utf-8输出
                String str = new String(line.getBytes(), StandardCharsets.UTF_8);
                System.out.println(str);
            }
            // 等待Whisper进程完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 转录成功，transcription包含中文字幕
                long time = System.currentTimeMillis() - start;
                System.out.println("耗时: " + time + "ms");
                log.info("转录成功！");
                String[] split = localFile.getName().split("\\.");
                reader = new BufferedReader(new FileReader(outputDir+split[0]+"."+outputFormat));
               /* result.append("解析结果:\n");*/
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                System.out.println(result);
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
