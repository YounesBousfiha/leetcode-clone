package com.leetcode.aiassistant.application.service.impl;

import com.leetcode.aiassistant.application.service.IAIAssistService;
import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import com.leetcode.aiassistant.presentation.dto.CodeReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI Assist Service using Spring AI with DeepSeek
 * Simple, clean, and maintainable implementation
 * With Redis caching to reduce LLM API costs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIAssistService implements IAIAssistService {

    private final ChatClient chatClient;

    @Override
    @Cacheable(value = "ai_hints", key = "#problemSlug + ':' + #language", unless = "#result == null")
    public AIHintResponse getHint(String problemSlug, String userCode, String language) {
        log.info("Getting hint for problem: {} in language: {} (not cached)", problemSlug, language);

        try {
            String prompt = buildHintPrompt(problemSlug, userCode, language);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("Received hint from DeepSeek");
            return parseHintResponse(response);

        } catch (Exception e) {
            log.error("Error generating hint: {}", e.getMessage(), e);
            return AIHintResponse.builder()
                    .hint("Unable to generate hint at this time. Please try again later.")
                    .complexity("Not available")
                    .approach("Try breaking down the problem into smaller steps")
                    .build();
        }
    }

    @Override
    @Cacheable(value = "ai_explanations", key = "#problemSlug", unless = "#result == null")
    public AIHintResponse explainProblem(String problemSlug) {
        log.info("Explaining problem: {} (not cached)", problemSlug);

        try {
            String prompt = buildExplainPrompt(problemSlug);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("Received explanation from DeepSeek");
            return parseHintResponse(response);

        } catch (Exception e) {
            log.error("Error explaining problem: {}", e.getMessage(), e);
            return AIHintResponse.builder()
                    .hint("This is a coding challenge problem. Try to understand the input/output requirements first.")
                    .complexity("Varies by approach")
                    .approach("Analyze the problem constraints and think of suitable data structures")
                    .build();
        }
    }

    @Override
    public CodeReviewResponse reviewCode(String code, String language, String problemSlug) {
        log.info("Reviewing code for problem: {} in language: {}", problemSlug, language);

        try {
            String prompt = buildReviewPrompt(code, language, problemSlug);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("Received code review from DeepSeek");
            return parseReviewResponse(response);

        } catch (Exception e) {
            log.error("Error reviewing code: {}", e.getMessage(), e);
            return CodeReviewResponse.builder()
                    .overallFeedback("Unable to review code at this time. Please ensure your code is syntactically correct.")
                    .improvements(List.of("Consider testing your code with edge cases"))
                    .bugs(List.of("Manual review recommended"))
                    .codeQualityScore(70)
                    .build();
        }
    }

    // ========== Prompt Templates ==========

    private String buildHintPrompt(String problemSlug, String userCode, String language) {
        return String.format("""
                I'm working on a coding problem titled "%s" and I'm stuck.
                
                Here's my current solution in %s:
                ```%s
                %s
                ```
                
                Please provide:
                1. A helpful hint (not the complete solution) to guide me in the right direction
                2. The time and space complexity of the optimal approach
                3. The general approach or algorithm pattern I should consider
                
                Format your response as:
                HINT: [your hint here]
                COMPLEXITY: [time and space complexity]
                APPROACH: [algorithm pattern or approach name]
                """, problemSlug, language, language, userCode);
    }

    private String buildExplainPrompt(String problemSlug) {
        return String.format("""
                Explain this coding problem in a clear and concise way:
                
                Problem: %s
                
                Please provide:
                1. A brief explanation of what the problem is asking
                2. The optimal time and space complexity
                3. The recommended approach or algorithm pattern to solve it
                
                Format your response as:
                HINT: [explanation of the problem]
                COMPLEXITY: [optimal time and space complexity]
                APPROACH: [recommended approach or pattern]
                """, problemSlug);
    }

    private String buildReviewPrompt(String code, String language, String problemSlug) {
        return String.format("""
                Please review this code submission for the problem "%s":
                
                Language: %s
                ```%s
                %s
                ```
                
                Provide a structured code review with:
                1. Overall feedback (2-3 sentences)
                2. List of potential improvements
                3. List of potential bugs or issues
                4. Code quality score out of 100
                
                Format your response as:
                FEEDBACK: [overall feedback]
                IMPROVEMENTS:
                - [improvement 1]
                - [improvement 2]
                BUGS:
                - [bug 1]
                - [bug 2]
                SCORE: [number between 0-100]
                """, problemSlug, language, language, code);
    }

    // ========== Response Parsers ==========

    private AIHintResponse parseHintResponse(String content) {
        try {
            String hint = extractSection(content, "HINT:");
            String complexity = extractSection(content, "COMPLEXITY:");
            String approach = extractSection(content, "APPROACH:");

            return AIHintResponse.builder()
                    .hint(hint != null ? hint : content)
                    .complexity(complexity != null ? complexity : "Not specified")
                    .approach(approach != null ? approach : "General problem solving")
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse structured response, returning raw content");
            return AIHintResponse.builder()
                    .hint(content)
                    .complexity("Not specified")
                    .approach("General problem solving")
                    .build();
        }
    }

    private CodeReviewResponse parseReviewResponse(String content) {
        try {
            String feedback = extractSection(content, "FEEDBACK:");
            List<String> improvements = extractList(content, "IMPROVEMENTS:");
            List<String> bugs = extractList(content, "BUGS:");
            Integer score = extractScore(content);

            return CodeReviewResponse.builder()
                    .overallFeedback(feedback != null ? feedback : "Code reviewed")
                    .improvements(improvements.isEmpty() ? List.of("No specific improvements suggested") : improvements)
                    .bugs(bugs.isEmpty() ? List.of("No bugs detected") : bugs)
                    .codeQualityScore(score != null ? score : 70)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse review response, returning default");
            return CodeReviewResponse.builder()
                    .overallFeedback(content)
                    .improvements(List.of("Review the code structure and efficiency"))
                    .bugs(List.of("Manual review recommended"))
                    .codeQualityScore(70)
                    .build();
        }
    }

    private String extractSection(String content, String sectionName) {
        try {
            Pattern pattern = Pattern.compile(sectionName + "\\s*(.+?)(?=(\\n[A-Z]+:|$))", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            log.debug("Could not extract section: {}", sectionName);
        }
        return null;
    }

    private List<String> extractList(String content, String sectionName) {
        List<String> items = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(sectionName + "\\s*(.+?)(?=(\\n[A-Z]+:|$))", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String listContent = matcher.group(1).trim();
                String[] lines = listContent.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("-") || line.startsWith("•") || line.startsWith("*")) {
                        items.add(line.substring(1).trim());
                    } else if (!line.isEmpty() && line.matches("^\\d+\\..*")) {
                        items.add(line.replaceFirst("^\\d+\\.\\s*", "").trim());
                    } else if (!line.isEmpty()) {
                        items.add(line);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract list: {}", sectionName);
        }
        return items;
    }

    private Integer extractScore(String content) {
        try {
            Pattern pattern = Pattern.compile("SCORE:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            log.debug("Could not extract score");
        }
        return null;
    }
}

