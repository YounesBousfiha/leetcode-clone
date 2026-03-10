package com.leetcode.judgeservice.infrastructure.seeder;

import com.leetcode.judgeservice.domain.entity.Submission;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import com.leetcode.judgeservice.domain.enums.SubmissionStatus;
import com.leetcode.judgeservice.infrastructure.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
public class SubmissionSeeder implements CommandLineRunner {

    private final SubmissionRepository submissionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (submissionRepository.count() > 0) {
            log.info("✅ Submissions already seeded");
            return;
        }

        log.info("🌱 Seeding submissions...");

        seedSubmissions();

        log.info("✅ Submissions seeding completed!");
    }

    private void seedSubmissions() {
        List<Submission> submissions = new ArrayList<>();

        // Sample user IDs (these should match the user IDs from auth-service in production)
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID user3 = UUID.randomUUID();

        // Sample problem slugs/IDs
        String[] problemIds = {"two-sum", "add-two-numbers", "valid-parentheses", "palindrome-number"};

        // Accepted submission - Two Sum in Java
        submissions.add(createSubmission(user1, problemIds[0], ProgrammingLanguage.JAVA,
                SubmissionStatus.ACCEPTED,
                "class Solution {\n    public int[] twoSum(int[] nums, int target) {\n" +
                "        Map<Integer, Integer> map = new HashMap<>();\n" +
                "        for (int i = 0; i < nums.length; i++) {\n" +
                "            int complement = target - nums[i];\n" +
                "            if (map.containsKey(complement)) {\n" +
                "                return new int[] { map.get(complement), i };\n" +
                "            }\n" +
                "            map.put(nums[i], i);\n" +
                "        }\n" +
                "        return new int[] {};\n" +
                "    }\n}",
                45.5, 42000L, 3, 3));

        // Accepted submission - Valid Parentheses in Python
        submissions.add(createSubmission(user2, problemIds[2], ProgrammingLanguage.PYTHON,
                SubmissionStatus.ACCEPTED,
                "class Solution:\n    def isValid(self, s: str) -> bool:\n" +
                "        stack = []\n" +
                "        mapping = {')': '(', '}': '{', ']': '['}\n" +
                "        for char in s:\n" +
                "            if char in mapping:\n" +
                "                top = stack.pop() if stack else '#'\n" +
                "                if mapping[char] != top:\n" +
                "                    return False\n" +
                "            else:\n" +
                "                stack.append(char)\n" +
                "        return not stack",
                32.0, 38000L, 4, 4));

        // Wrong Answer submission - Two Sum in JavaScript
        submissions.add(createSubmission(user3, problemIds[0], ProgrammingLanguage.JAVASCRIPT,
                SubmissionStatus.WRONG_ANSWER,
                "var twoSum = function(nums, target) {\n" +
                "    for (let i = 0; i < nums.length; i++) {\n" +
                "        for (let j = i; j < nums.length; j++) {\n" + // Bug: should be j = i + 1
                "            if (nums[i] + nums[j] === target) {\n" +
                "                return [i, j];\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    return [];\n" +
                "};",
                25.0, 35000L, 3, 1));

        // Time Limit Exceeded submission - Add Two Numbers in Java
        submissions.add(createSubmission(user1, problemIds[1], ProgrammingLanguage.JAVA,
                SubmissionStatus.TIME_LIMIT_EXCEEDED,
                "class Solution {\n    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {\n" +
                "        // Inefficient implementation that causes TLE\n" +
                "        while(true) {\n" +
                "            // Infinite loop bug\n" +
                "        }\n" +
                "    }\n}",
                1500.0, 50000L, 5, 0));

        // Runtime Error submission - Palindrome Number in Python
        submissions.add(createSubmission(user2, problemIds[3], ProgrammingLanguage.PYTHON,
                SubmissionStatus.RUNTIME_ERROR,
                "class Solution:\n    def isPalindrome(self, x: int) -> bool:\n" +
                "        s = str(x)\n" +
                "        return s == s[::-1]  # Will fail for negative numbers",
                0.0, 0L, 3, 0));

        // Accepted submission - Palindrome Number in C++
        submissions.add(createSubmission(user3, problemIds[3], ProgrammingLanguage.CPP,
                SubmissionStatus.ACCEPTED,
                "class Solution {\npublic:\n    bool isPalindrome(int x) {\n" +
                "        if (x < 0) return false;\n" +
                "        long reversed = 0, temp = x;\n" +
                "        while (temp != 0) {\n" +
                "            reversed = reversed * 10 + temp % 10;\n" +
                "            temp /= 10;\n" +
                "        }\n" +
                "        return reversed == x;\n" +
                "    }\n};",
                38.0, 40000L, 3, 3));

        // Compilation Error submission - Two Sum in Java
        submissions.add(createSubmission(user1, problemIds[0], ProgrammingLanguage.JAVA,
                SubmissionStatus.COMPILATION_ERROR,
                "class Solution {\n    public int[] twoSum(int[] nums, int target) {\n" +
                "        Map<Integer, Integer> map = new HashMap<>(\n" + // Missing closing parenthesis
                "        // Incomplete code\n",
                0.0, 0L, 0, 0));

        // Accepted submission - Valid Parentheses in Go
        submissions.add(createSubmission(user2, problemIds[2], ProgrammingLanguage.GO,
                SubmissionStatus.ACCEPTED,
                "func isValid(s string) bool {\n" +
                "    stack := []rune{}\n" +
                "    pairs := map[rune]rune{')': '(', '}': '{', ']': '['}\n" +
                "    for _, char := range s {\n" +
                "        if open, ok := pairs[char]; ok {\n" +
                "            if len(stack) == 0 || stack[len(stack)-1] != open {\n" +
                "                return false\n" +
                "            }\n" +
                "            stack = stack[:len(stack)-1]\n" +
                "        } else {\n" +
                "            stack = append(stack, char)\n" +
                "        }\n" +
                "    }\n" +
                "    return len(stack) == 0\n}",
                28.5, 36000L, 4, 4));

        submissionRepository.saveAll(submissions);
        log.info("✅ Seeded {} submissions", submissions.size());
    }

    private Submission createSubmission(UUID userId, String problemId, ProgrammingLanguage language,
                                       SubmissionStatus status, String code,
                                       double executionTime, long memoryUsed,
                                       int totalTestCases, int passedTestCases) {
        LocalDateTime now = LocalDateTime.now();

        Submission submission = Submission.builder()
                .userID(userId)
                .problemId(problemId)
                .language(language)
                .status(status)
                .code(code)
                .executionTime(executionTime)
                .memoryUsed(memoryUsed)
                .completedAt(status != SubmissionStatus.PENDING ? now.minusMinutes(10) : null)
                .build();

        // Add submission results for completed submissions
        if (status != SubmissionStatus.PENDING && status != SubmissionStatus.COMPILATION_ERROR) {
            for (int i = 0; i < totalTestCases; i++) {
                boolean passed = i < passedTestCases;
                SubmissionResult result = SubmissionResult.builder()
                        .testCaseId("test-case-" + (i + 1))
                        .passed(passed)
                        .output(passed ? "Correct output" : "Wrong output")
                        .expectedOutput("Expected output " + (i + 1))
                        .executionTime(executionTime / totalTestCases)
                        .memoryUsed(memoryUsed / totalTestCases)
                        .errorMessage(passed ? null : getErrorMessage(status))
                        .build();
                submission.addResult(result);
            }
        }

        return submission;
    }

    private String getErrorMessage(SubmissionStatus status) {
        return switch (status) {
            case WRONG_ANSWER -> "Output doesn't match expected result";
            case TIME_LIMIT_EXCEEDED -> "Execution time exceeded limit";
            case RUNTIME_ERROR -> "Runtime exception occurred during execution";
            case MEMORY_LIMIT_EXCEEDED -> "Memory usage exceeded limit";
            default -> null;
        };
    }
}

