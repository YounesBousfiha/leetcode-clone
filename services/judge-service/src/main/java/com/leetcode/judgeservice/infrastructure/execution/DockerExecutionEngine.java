package com.leetcode.judgeservice.infrastructure.execution;

import com.leetcode.judgeservice.application.service.ICodeExecutionEngine;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.netflix.discovery.provider.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DockerExecutionEngine implements ICodeExecutionEngine {

    private static final String IMAGE = "openjdk:17-alpine";
    private static final String TEMP_DIR = System.getProperty("user.dir") + "/temp";
    @Override
    public SubmissionResult executeCode(String userCode, String language, String input, String expectedOutput) {
        String executionId = UUID.randomUUID().toString();
        Path folder = Path.of(TEMP_DIR, executionId);


        try {

            Files.createDirectories(folder);
            createMainFile(folder, userCode);

            ProcessBuilder builder = new ProcessBuilder(
                    "docker", "run", "--rm",
                    "-v", folder.toAbsolutePath() + ":/app",
                    "-w", "/app",
                    IMAGE,
                    "/bin/sh", "-c", "javac Main.java && java Main"
            );

            Process process = builder.start();

            String output = readStream(process.getInputStream());
            String error = readStream(process.getErrorStream());

            boolean finished = process.waitFor(3, TimeUnit.SECONDS);

            if(!finished) {
                process.destroy();
                return buildResult(false, "Time Limit Exceeded", 3000.0);
            }

            if(process.exitValue() != 0) {
                return buildResult(false, "Runtime Error: " + error, 0.0);
            }

            boolean passed = output.trim().equals(expectedOutput.trim());
            return buildResult(passed, output.trim(), 100.0);

        } catch (Exception e) {
            log.error("Execution failed", e);
            return buildResult(false, "System Error", 0.0);
        } finally {
            deleteFolder(folder.toFile());
        }
    }

    private void createMainFile(Path folder, String code) throws IOException {
        String content = "public class Main { public static void main(String[] args) { " + code +"}}";
        Files.writeString(folder.resolve("Main.java"), content);
    }

    private String readStream(InputStream s) throws IOException {
        return new String(s.readAllBytes());
    }

    private SubmissionResult buildResult(boolean passed, String output, Double time) {
        return SubmissionResult.builder()
                .passed(passed)
                .output(output)
                .executionTime(time)
                .build();
    }

    private void deleteFolder(File file) {
        /* */
    }
}
