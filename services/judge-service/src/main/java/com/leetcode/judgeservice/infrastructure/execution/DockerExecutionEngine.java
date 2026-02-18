package com.leetcode.judgeservice.infrastructure.execution;

import com.leetcode.judgeservice.application.service.ICodeExecutionEngine;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import com.leetcode.judgeservice.domain.exception.CodeExecutionException;
import com.leetcode.judgeservice.infrastructure.execution.strategy.ILanguagesStrategy;
import com.netflix.discovery.provider.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DockerExecutionEngine implements ICodeExecutionEngine {

    private final Map<ProgrammingLanguage, ILanguagesStrategy> strategies;
    private static final String TEMP_DIR = System.getProperty("user.dir") + "/temp";


    public DockerExecutionEngine(List<ILanguagesStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ILanguagesStrategy::getLanguage, Function.identity()));
    }

    @Override
    public SubmissionResult executeCode(String userCode, String language, String input, String expectedOutput) {
        ProgrammingLanguage langEnum;

        try {
            langEnum = ProgrammingLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CodeExecutionException("Invalid language format: " + language);
        }

        ILanguagesStrategy strategy = strategies.get(langEnum);
        if(strategy == null) {
            throw new CodeExecutionException("Language strategy not found for: " + language);
        }

        String executionId = UUID.randomUUID().toString();
        Path folder = Path.of(TEMP_DIR, executionId);

        try {
            Files.createDirectories(folder);

            String wrappedCode = strategy.wrapCode(userCode);
            String fileName = strategy.getFileName();

            Files.writeString(folder.resolve(fileName), wrappedCode);

            ProcessBuilder builder = new ProcessBuilder(
                    "docker", "run", "--rm",
                    "--memory=256m",
                    "--cpus=0.5",
                    "-v", folder.toAbsolutePath() + ":/app",
                    "-w", "/app",
                    strategy.getDockerImage(),
                    "/bin/sh", "-c", strategy.getRunCommand()
            );

            Process process = builder.start();

            boolean finished = process.waitFor(3, TimeUnit.SECONDS);

            if(!finished) {
                process.destroy();
                return buildResult(false, "Time Limit Exceeded", 3000.0);
            }

            String output = readStream(process.getInputStream());
            String error = readStream(process.getErrorStream());

            if(process.exitValue() != 0) {
                return buildResult(false, "Runtime Error: " + error + "\n" + output, 0.0);
            }

            boolean passed = output.trim().equals(expectedOutput.trim());

            return buildResult(passed, output.trim(), 100.0);
        } catch (Exception e) {
            log.error("Execution failed for ID: " + executionId, e);
            return buildResult(false , "System Error: " + e.getMessage(), 0.0);
        } finally {
            deleteFolder(folder.toFile());
        }
    }

    private String readStream(InputStream s) throws IOException {
        try (s) {
            return new String(s.readAllBytes());
        }
    }

    private SubmissionResult buildResult(boolean passed, String output, Double time) {
        return SubmissionResult.builder()
                .passed(passed)
                .output(output)
                .executionTime(time)
                .build();
    }

    private void deleteFolder(File directory) {
        if(directory.exists()) {
            File[] files = directory.listFiles();

            if(files != null) {
                for(File file : files) {
                    if(file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }

        directory.delete();
    }
}
