package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import org.springframework.stereotype.Component;

@Component
public class PythonStrategy  implements ILanguagesStrategy{
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.PYTHON;
    }

    @Override
    public String getDockerImage() {
        return "python:3.9-alpine";
    }

    @Override
    public String getFileExtension() {
        return ".py";
    }

    @Override
    public String getRunCommand() {
        return "python3 main.py";
    }

    @Override
    public String wrapCode(String userCode) {
        return "import sys\n" +
                "import math\n" +
                userCode;
    }

    @Override
    public String getFileName() {
        return "main" + getFileExtension();
    }
}
