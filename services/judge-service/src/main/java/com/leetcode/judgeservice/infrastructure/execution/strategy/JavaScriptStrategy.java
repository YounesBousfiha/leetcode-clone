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
    public String wrapCode(String userCode, String input) {
        String escapedInput = input != null ? input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") : "";
        return userCode + "\n\n" +
                "const input = \"" + escapedInput + "\";\n" +
                "const lines = input.split('\\n');\n" +
                "const solution = new (Object.getPrototypeOf(eval('(' + " + "userCode" + " + ')')).constructor)();\n" +
                "// Get the first method name from the Solution object\n" +
                "const methodName = Object.getOwnPropertyNames(Object.getPrototypeOf(new Solution())).filter(m => m !== 'constructor')[0];\n" +
                "if (methodName) {\n" +
                "    const args = lines.map(line => {\n" +
                "        try { return JSON.parse(line.trim()); } catch(e) { return line.trim(); }\n" +
                "    });\n" +
                "    const result = new Solution()[methodName](...args);\n" +
                "    console.log(JSON.stringify(result));\n" +
                "}\n";
    }

    @Override
    public String getFileName() {
        return "solution" + getFileExtension();
    }
}

