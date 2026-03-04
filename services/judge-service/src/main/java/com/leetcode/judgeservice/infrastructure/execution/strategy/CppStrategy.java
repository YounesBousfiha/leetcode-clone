package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import org.springframework.stereotype.Component;

@Component
public class CppStrategy implements ILanguagesStrategy {
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.CPP;
    }

    @Override
    public String getDockerImage() {
        return "gcc:12-alpine";
    }

    @Override
    public String getFileExtension() {
        return ".cpp";
    }

    @Override
    public String getRunCommand() {
        return "/bin/sh -c \"g++ -std=c++17 -O2 solution.cpp -o solution && ./solution\"";
    }

    @Override
    public String wrapCode(String userCode) {
        return "#include <iostream>\n" +
                "#include <vector>\n" +
                "#include <string>\n" +
                "#include <algorithm>\n" +
                "#include <map>\n" +
                "#include <set>\n" +
                "#include <unordered_map>\n" +
                "#include <unordered_set>\n" +
                "using namespace std;\n\n" +
                userCode;
    }

    @Override
    public String getFileName() {
        return "solution" + getFileExtension();
    }
}

