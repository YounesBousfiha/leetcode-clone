package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import org.springframework.stereotype.Component;

@Component
public class GoStrategy implements ILanguagesStrategy {
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.GO;
    }

    @Override
    public String getDockerImage() {
        return "golang:1.21-alpine";
    }

    @Override
    public String getFileExtension() {
        return ".go";
    }

    @Override
    public String getRunCommand() {
        return "go run main.go";
    }

    @Override
    public String wrapCode(String userCode) {
        return "package main\n\n" +
                "import (\n" +
                "    \"fmt\"\n" +
                ")\n\n" +
                userCode;
    }

    @Override
    public String getFileName() {
        return "main" + getFileExtension();
    }
}

