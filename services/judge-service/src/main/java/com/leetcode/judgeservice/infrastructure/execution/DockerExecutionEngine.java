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
import java.util.ArrayList;
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
    private final Path tempBaseDir;
    private final String sharedDockerVolume;
    private final String dockerExecutable;


    public DockerExecutionEngine(List<ILanguagesStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ILanguagesStrategy::getLanguage, Function.identity()));

        String configuredTempDir = System.getenv("JUDGE_TEMP_DIR");
        if (configuredTempDir == null || configuredTempDir.isBlank()) {
            configuredTempDir = Path.of(System.getProperty("java.io.tmpdir"), "judge-service").toString();
        }
        this.tempBaseDir = Path.of(configuredTempDir);

        String configuredVolume = System.getenv("JUDGE_DOCKER_SHARED_VOLUME");
        this.sharedDockerVolume = (configuredVolume == null || configuredVolume.isBlank()) ? null : configuredVolume;

        String configuredExecutable = System.getenv("JUDGE_DOCKER_EXECUTABLE");
        this.dockerExecutable = (configuredExecutable == null || configuredExecutable.isBlank())
                ? "docker"
                : configuredExecutable;
    }

    @PostConstruct
    public void pullDockerImages() {
        ensureTempBaseDirectoryReady();
        ensureDockerCliIsReachable();
        log.info("Judge temp directory ready: {}", tempBaseDir.toAbsolutePath());
        log.info("Using Docker executable: {}", dockerExecutable);
        log.info("Pre-pulling Docker images for all supported languages...");
        
        strategies.values().forEach(strategy -> {
            String imageName = strategy.getDockerImage();
            log.info("Pulling Docker image: {}", imageName);
            
            try {
                ProcessBuilder builder = new ProcessBuilder(dockerExecutable, "pull", imageName);
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
        Path folder = tempBaseDir.resolve(executionId);

        try {
            ensureTempBaseDirectoryReady();
            Files.createDirectories(folder);

            String wrappedCode = strategy.wrapCode(userCode, input);
            String fileName = strategy.getFileName();

            Files.writeString(folder.resolve(fileName), wrappedCode);
            List<String> command = new ArrayList<>(List.of(
                    dockerExecutable, "run",
                    "--rm",
                    "--memory=256m",
                    "--cpus=0.5"
            ));

            if (sharedDockerVolume != null) {
                // Use a named volume in Docker Compose so judge-service and runner containers see the same files.
                command.add("-v");
                command.add(sharedDockerVolume + ":/workspace");
                command.add("-w");
                command.add("/workspace/" + executionId);
            } else {
                command.add("-v");
                command.add(folder.toAbsolutePath() + ":/app");
                command.add("-w");
                command.add("/app");
            }

            command.add(strategy.getDockerImage());
            command.add("/bin/sh");
            command.add("-c");
            command.add(strategy.getRunCommand());

            ProcessBuilder builder = new ProcessBuilder(command);

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

    private void ensureTempBaseDirectoryReady() {
        try {
            Files.createDirectories(tempBaseDir);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Judge temp directory is not accessible: " + tempBaseDir.toAbsolutePath()
                            + ". Verify the mounted volume permissions for JUDGE_TEMP_DIR.",
                    e
            );
        }

        if (!Files.isDirectory(tempBaseDir) || !Files.isWritable(tempBaseDir)) {
            throw new IllegalStateException(
                    "Judge temp directory is not writable: " + tempBaseDir.toAbsolutePath()
                            + ". Verify the mounted volume permissions for JUDGE_TEMP_DIR."
            );
        }
    }

    private void ensureDockerCliIsReachable() {
        try {
            Process process = new ProcessBuilder(dockerExecutable, "info", "--format", "{{.ServerVersion}}")
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new IllegalStateException(
                        "Docker daemon did not respond using '" + dockerExecutable
                                + "' within 10 seconds. Ensure /var/run/docker.sock is mounted and accessible."
                );
            }

            String dockerServerVersion = readStream(process.getInputStream()).trim();

            if (process.exitValue() != 0) {
                throw new IllegalStateException(
                        "Docker daemon is not reachable using '" + dockerExecutable
                                + "'. Ensure /var/run/docker.sock is mounted and accessible to judge-service."
                                + (dockerServerVersion.isBlank() ? "" : " Details: " + dockerServerVersion)
                );
            }

            log.info("Docker daemon reachable. Server version: {}", dockerServerVersion);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Docker executable not found: '" + dockerExecutable
                            + "'. Install docker-cli in judge-service image or set JUDGE_DOCKER_EXECUTABLE.",
                    e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while checking Docker CLI availability.", e);
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
