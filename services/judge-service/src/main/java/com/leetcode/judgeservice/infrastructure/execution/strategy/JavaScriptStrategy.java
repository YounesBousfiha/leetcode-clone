package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import org.springframework.stereotype.Component;

@Component
public class JavaScriptStrategy implements ILanguagesStrategy {
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.JAVASCRIPT;
    }

    @Override
    public String getDockerImage() {
        return "node:18-alpine";
    }

    @Override
    public String getFileExtension() {
        return ".js";
    }

    @Override
    public String getRunCommand() {
        return "node solution.js";
    }

    @Override
    public String wrapCode(String userCode) {
        return userCode;
    }

    @Override
    public String getFileName() {
        return "solution" + getFileExtension();
    }
}

