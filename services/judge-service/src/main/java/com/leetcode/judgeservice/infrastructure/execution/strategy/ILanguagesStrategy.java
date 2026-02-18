package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;

public interface ILanguagesStrategy {

    ProgrammingLanguage getLanguage();

    String getDockerImage();

    String getFileExtension();

    String getRunCommand();

    String wrapCode(String userCode);

    String getFileName();
}
