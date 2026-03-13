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
    public String wrapCode(String userCode, String input) {
        String escapedInput = input != null ? input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") : "";
        return "import sys\n" +
                "import math\n" +
                "import json\n" +
                "import ast\n\n" +
                userCode + "\n\n" +
                "if __name__ == '__main__':\n" +
                "    input_str = \"" + escapedInput + "\"\n" +
                "    lines = input_str.split('\\n')\n" +
                "    solution = Solution()\n" +
                "    # Find the main method in Solution class\n" +
                "    methods = [m for m in dir(solution) if not m.startswith('_') and callable(getattr(solution, m))]\n" +
                "    if methods:\n" +
                "        method = getattr(solution, methods[0])\n" +
                "        import inspect\n" +
                "        params = list(inspect.signature(method).parameters.keys())\n" +
                "        args = []\n" +
                "        for i, line in enumerate(lines):\n" +
                "            if i < len(params):\n" +
                "                try:\n" +
                "                    args.append(ast.literal_eval(line.strip()))\n" +
                "                except:\n" +
                "                    args.append(line.strip())\n" +
                "        result = method(*args)\n" +
                "        if isinstance(result, list):\n" +
                "            print(str(result).replace(' ', ''))\n" +
                "        else:\n" +
                "            print(result)\n";
    }

    @Override
    public String getFileName() {
        return "main" + getFileExtension();
    }
}
