package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import org.springframework.stereotype.Component;

@Component
public class JavaStrategy implements ILanguagesStrategy{
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.JAVA;
    }

    @Override
    public String getDockerImage() {
        return "openjdk:17-alpine";
    }

    @Override
    public String getFileExtension() {
        return ".java";
    }

    @Override
    public String getRunCommand() {
        return "/bin/sh -c \"javac Main.java && java Main\"";
    }

    @Override
    public String wrapCode(String userCode) {
        return "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        " + userCode + "\n" +
                "    }\n" +
                "}";
    }
}
