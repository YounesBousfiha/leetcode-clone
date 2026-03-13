package com.leetcode.judgeservice.infrastructure.execution;

import com.leetcode.judgeservice.application.service.ICodeExecutionEngine;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import com.leetcode.judgeservice.domain.exception.CodeExecutionException;
import com.leetcode.judgeservice.infrastructure.execution.strategy.ILanguagesStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @PostConstruct
    public void pullDockerImages() {
        log.info("Pre-pulling Docker images for all supported languages...");
        
        strategies.values().forEach(strategy -> {
            String imageName = strategy.getDockerImage();
            log.info("Pulling Docker image: {}", imageName);
            
            try {
                ProcessBuilder builder = new ProcessBuilder("docker", "pull", imageName);
                builder.inheritIO();
                Process process = builder.start();
                
                boolean finished = process.waitFor(5, TimeUnit.MINUTES);
                
                if (finished && process.exitValue() == 0) {
                    log.info("Successfully pulled image: {}", imageName);
                } else {
                    log.warn("Failed to pull image: {} (exit code: {})", imageName, 
                            finished ? process.exitValue() : "timeout");
                }
            } catch (Exception e) {
                log.error("Error pulling Docker image: {}", imageName, e);
            }
        });
        
        log.info("Docker image pre-pull completed.");
    }

    @Override
    public SubmissionResult executeCode(String userCode, String language, String input, String expectedOutput) {
        ProgrammingLanguage langEnum;
        long startNanos = System.nanoTime();

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

            String wrappedCode = strategy.wrapCode(userCode, input);
            String fileName = strategy.getFileName();

            Files.writeString(folder.resolve(fileName), wrappedCode);
            /* add --rm */
            ProcessBuilder builder = new ProcessBuilder(
                    "docker", "run",
                    "--memory=256m",
                    "--cpus=0.5",
                    "-v", folder.toAbsolutePath() + ":/app",
                    "-w", "/app",
                    strategy.getDockerImage(),
                    "/bin/sh", "-c", strategy.getRunCommand()
            );

            Process process = builder.start();


            boolean finished = process.waitFor(10, TimeUnit.SECONDS);


            if(!finished) {
                process.destroyForcibly();
                return buildResult(
                        false,
                        "Time Limit Exceeded",
                        expectedOutput,
                        elapsedMilliseconds(startNanos),
                        "Execution timed out after 10 seconds"
                );
            }

            String output = readStream(process.getInputStream());
            String error = readStream(process.getErrorStream());

            if(process.exitValue() != 0) {
                String runtimeMessage = "Runtime Error: " + error + "\n" + output;
                return buildResult(false, runtimeMessage, expectedOutput, elapsedMilliseconds(startNanos), error);
            }

            String trimmedOutput = output.trim();
            String normalizedOutput = normalizeForComparison(trimmedOutput);
            String normalizedExpected = normalizeForComparison(expectedOutput);
            boolean passed = false;
            if (normalizedExpected != null) {
                passed = normalizedExpected.equals(normalizedOutput);
            }

            return buildResult(passed, trimmedOutput, expectedOutput, elapsedMilliseconds(startNanos), null);
        } catch (Exception e) {
            log.error("Execution failed for ID: " + executionId, e);
            return buildResult(
                    false,
                    "System Error: " + e.getMessage(),
                    expectedOutput,
                    elapsedMilliseconds(startNanos),
                    e.getMessage()
            );
        } finally {
            deleteFolder(folder.toFile());
        }
    }

    private String readStream(InputStream s) throws IOException {
        try (s) {
            return new String(s.readAllBytes());
        }
    }

    private SubmissionResult buildResult(boolean passed, String output, String expectedOutput, Double time, String errorMessage) {
        return SubmissionResult.builder()
                .passed(passed)
                .output(output)
                .expectedOutput(expectedOutput)
                .executionTime(time)
                .errorMessage(errorMessage)
                .build();
    }

    private Double elapsedMilliseconds(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000.0;
    }

    private String normalizeForComparison(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (isStructuredOutput(trimmed)) {
            // Ignore formatting spaces for array/object-like outputs such as "[0, 1]" vs "[0,1]".
            return trimmed.replaceAll("\\s+", "");
        }

        return trimmed;
    }

    private boolean isStructuredOutput(String value) {
        return (value.startsWith("[") && value.endsWith("]"))
                || (value.startsWith("{") && value.endsWith("}"));
    }

    private void deleteFolder(File directory) {
        if(directory.exists()) {
            File[] files = directory.listFiles();

            if(files != null) {
                for(File file : files) {
                    if(file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        if (!file.delete()) {
                            log.debug("Unable to delete temporary file: {}", file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        if (!directory.delete()) {
            log.debug("Unable to delete temporary directory: {}", directory.getAbsolutePath());
        }
    }
}
