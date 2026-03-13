package com.leetcode.judgeservice.infrastructure.execution.strategy;

import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JavaStrategy implements ILanguagesStrategy {
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.JAVA;
    }

    @Override
    public String getDockerImage() {
        return "eclipse-temurin:21-jdk-jammy";
    }

    @Override
    public String getFileExtension() {
        return ".java";
    }

    @Override
    public String getRunCommand() {
        return "javac Main.java && java Main";
    }

    @Override
    public String wrapCode(String userCode, String input) {
        String escapedInput = escapeJavaString(input);

        StringBuilder sb = new StringBuilder();
        sb.append("import java.util.*;\n");
        sb.append("import java.util.stream.*;\n\n");
        sb.append(userCode).append("\n\n");
        sb.append("public class Main {\n");
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        String input = \"").append(escapedInput).append("\";\n");
        sb.append("        Solution solution = new Solution();\n");
        sb.append("        String[] lines = input.split(\"\\\\n\");\n");
        sb.append("        try {\n");
        sb.append("            java.lang.reflect.Method[] methods = Solution.class.getDeclaredMethods();\n");
        sb.append("            for (java.lang.reflect.Method method : methods) {\n");
        sb.append("                if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {\n");
        sb.append("                    Class<?>[] paramTypes = method.getParameterTypes();\n");
        sb.append("                    Object[] params = new Object[paramTypes.length];\n");
        sb.append("                    for (int i = 0; i < paramTypes.length && i < lines.length; i++) {\n");
        sb.append("                        params[i] = parseValue(lines[i].trim(), paramTypes[i]);\n");
        sb.append("                    }\n");
        sb.append("                    Object result = method.invoke(solution, params);\n");
        sb.append("                    System.out.println(formatOutput(result));\n");
        sb.append("                    return;\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("        } catch (Exception e) {\n");
        sb.append("            e.printStackTrace();\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        sb.append("    private static Object parseValue(String s, Class<?> type) {\n");
        sb.append("        s = s.trim();\n");
        sb.append("        if (type == int.class || type == Integer.class) {\n");
        sb.append("            return Integer.parseInt(s);\n");
        sb.append("        } else if (type == int[].class) {\n");
        sb.append("            s = s.replaceAll(\"[\\\\[\\\\]\\\\s]\", \"\");\n");
        sb.append("            if (s.isEmpty()) return new int[0];\n");
        sb.append("            return Arrays.stream(s.split(\",\")).mapToInt(Integer::parseInt).toArray();\n");
        sb.append("        } else if (type == String.class) {\n");
        sb.append("            if (s.startsWith(\"\\\"\") && s.endsWith(\"\\\"\")) {\n");
        sb.append("                return s.substring(1, s.length() - 1);\n");
        sb.append("            }\n");
        sb.append("            return s;\n");
        sb.append("        } else if (type == boolean.class || type == Boolean.class) {\n");
        sb.append("            return Boolean.parseBoolean(s);\n");
        sb.append("        } else if (type == long.class || type == Long.class) {\n");
        sb.append("            return Long.parseLong(s);\n");
        sb.append("        } else if (type == double.class || type == Double.class) {\n");
        sb.append("            return Double.parseDouble(s);\n");
        sb.append("        } else if (type == int[][].class) {\n");
        sb.append("            return parseIntMatrix(s);\n");
        sb.append("        } else if (type == List.class) {\n");
        sb.append("            return parseList(s);\n");
        sb.append("        }\n");
        sb.append("        return s;\n");
        sb.append("    }\n\n");
        sb.append("    private static int[][] parseIntMatrix(String s) {\n");
        sb.append("        s = s.trim();\n");
        sb.append("        if (s.equals(\"[]\")) return new int[0][0];\n");
        sb.append("        s = s.substring(1, s.length() - 1);\n");
        sb.append("        List<int[]> rows = new ArrayList<>();\n");
        sb.append("        int depth = 0;\n");
        sb.append("        StringBuilder sb2 = new StringBuilder();\n");
        sb.append("        for (char c : s.toCharArray()) {\n");
        sb.append("            if (c == '[') { depth++; sb2.append(c); }\n");
        sb.append("            else if (c == ']') { depth--; sb2.append(c); if (depth == 0) { rows.add((int[])parseValue(sb2.toString(), int[].class)); sb2 = new StringBuilder(); } }\n");
        sb.append("            else if (c == ',' && depth == 0) { continue; }\n");
        sb.append("            else { sb2.append(c); }\n");
        sb.append("        }\n");
        sb.append("        return rows.toArray(new int[0][]);\n");
        sb.append("    }\n\n");
        sb.append("    private static List<Integer> parseList(String s) {\n");
        sb.append("        s = s.replaceAll(\"[\\\\[\\\\]\\\\s]\", \"\");\n");
        sb.append("        if (s.isEmpty()) return new ArrayList<>();\n");
        sb.append("        return Arrays.stream(s.split(\",\")).map(Integer::parseInt).collect(Collectors.toList());\n");
        sb.append("    }\n\n");
        sb.append("    private static String formatOutput(Object result) {\n");
        sb.append("        if (result == null) return \"null\";\n");
        sb.append("        if (result instanceof int[]) {\n");
        sb.append("            return Arrays.toString((int[]) result);\n");
        sb.append("        } else if (result instanceof int[][]) {\n");
        sb.append("            return Arrays.deepToString((int[][]) result);\n");
        sb.append("        } else if (result instanceof long[]) {\n");
        sb.append("            return Arrays.toString((long[]) result);\n");
        sb.append("        } else if (result instanceof String[]) {\n");
        sb.append("            return Arrays.toString((String[]) result);\n");
        sb.append("        } else if (result instanceof List) {\n");
        sb.append("            return result.toString();\n");
        sb.append("        } else if (result instanceof Boolean) {\n");
        sb.append("            return result.toString();\n");
        sb.append("        }\n");
        sb.append("        return String.valueOf(result);\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String escapeJavaString(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String getFileName() {
        return "Main" + getFileExtension();
    }
}
