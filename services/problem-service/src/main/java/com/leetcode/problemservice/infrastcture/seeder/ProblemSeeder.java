package com.leetcode.problemservice.infrastcture.seeder;

import com.leetcode.problemservice.domain.entity.CodeTemplate;
import com.leetcode.problemservice.domain.entity.Problem;
import com.leetcode.problemservice.domain.entity.Tag;
import com.leetcode.problemservice.domain.entity.TestCase;
import com.leetcode.problemservice.domain.enums.Difficulty;
import com.leetcode.problemservice.infrastcture.repository.ProblemRepository;
import com.leetcode.problemservice.infrastcture.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
public class ProblemSeeder implements CommandLineRunner {

    private final ProblemRepository problemRepository;
    private final TagRepository tagRepository;

    @Override
    public void run(String... args) throws Exception {
        if (problemRepository.count() > 0) {
            log.info("✅ Problems already seeded");
            return;
        }

        log.info("🌱 Seeding problems...");

        // Seed tags first
        Map<String, Tag> tags = seedTags();

        // Seed problems
        seedProblems(tags);

        log.info("✅ Problems seeding completed!");
    }

    private Map<String, Tag> seedTags() {
        Map<String, Tag> tagMap = new HashMap<>();

        String[][] tagData = {
            {"Array", "array"},
            {"String", "string"},
            {"Hash Table", "hash-table"},
            {"Dynamic Programming", "dynamic-programming"},
            {"Math", "math"},
            {"Sorting", "sorting"},
            {"Greedy", "greedy"},
            {"Depth-First Search", "depth-first-search"},
            {"Binary Search", "binary-search"},
            {"Tree", "tree"},
            {"Breadth-First Search", "breadth-first-search"},
            {"Two Pointers", "two-pointers"},
            {"Linked List", "linked-list"},
            {"Stack", "stack"},
            {"Queue", "queue"}
        };

        for (String[] data : tagData) {
            Tag tag = Tag.builder()
                    .name(data[0])
                    .slug(data[1])
                    .build();
            tagMap.put(data[1], tagRepository.save(tag));
        }

        log.info("✅ Seeded {} tags", tagMap.size());
        return tagMap;
    }

    private void seedProblems(Map<String, Tag> tags) {
        List<Problem> problems = new ArrayList<>();

        // Problem 1: Two Sum
        Problem twoSum = createTwoSumProblem(tags);
        problems.add(twoSum);

        // Problem 2: Add Two Numbers
        Problem addTwoNumbers = createAddTwoNumbersProblem(tags);
        problems.add(addTwoNumbers);

        // Problem 3: Longest Substring Without Repeating Characters
        Problem longestSubstring = createLongestSubstringProblem(tags);
        problems.add(longestSubstring);

        // Problem 4: Median of Two Sorted Arrays
        Problem medianArrays = createMedianArraysProblem(tags);
        problems.add(medianArrays);

        // Problem 5: Palindrome Number
        Problem palindromeNumber = createPalindromeNumberProblem(tags);
        problems.add(palindromeNumber);

        // Problem 6: Valid Parentheses
        Problem validParentheses = createValidParenthesesProblem(tags);
        problems.add(validParentheses);

        // Problem 7: Merge Two Sorted Lists
        Problem mergeLists = createMergeSortedListsProblem(tags);
        problems.add(mergeLists);

        // Problem 8: Maximum Subarray
        Problem maxSubarray = createMaxSubarrayProblem(tags);
        problems.add(maxSubarray);

        problemRepository.saveAll(problems);
        log.info("✅ Seeded {} problems", problems.size());
    }

    private Problem createTwoSumProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Two Sum")
                .slug("two-sum")
                .description("Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.\n\n" +
                        "You may assume that each input would have exactly one solution, and you may not use the same element twice.\n\n" +
                        "You can return the answer in any order.\n\n" +
                        "**Example 1:**\n```\nInput: nums = [2,7,11,15], target = 9\nOutput: [0,1]\nExplanation: Because nums[0] + nums[1] == 9, we return [0, 1].\n```")
                .difficulty(Difficulty.EASY)
                .timeLimit(1.0)
                .memoryLimit(256)
                .tags(new HashSet<>(Arrays.asList(tags.get("array"), tags.get("hash-table"))))
                .hints(Arrays.asList(
                        "Think about using a hash map to store values you've seen",
                        "For each number, check if target - number exists in the hash map"
                ))
                .build();

        // Test Cases
        problem.addTestCase(TestCase.builder()
                .input("[2,7,11,15]\n9")
                .expectedOutput("[0,1]")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[3,2,4]\n6")
                .expectedOutput("[1,2]")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[3,3]\n6")
                .expectedOutput("[0,1]")
                .isPublic(false)
                .build());

        // Code Templates
        addCodeTemplates(problem,
            "class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def twoSum(self, nums: List[int], target: int) -> List[int]:\n        # Your code here\n        pass",
            "/**\n * @param {number[]} nums\n * @param {number} target\n * @return {number[]}\n */\nvar twoSum = function(nums, target) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    vector<int> twoSum(vector<int>& nums, int target) {\n        // Your code here\n    }\n};",
            "func twoSum(nums []int, target int) []int {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createAddTwoNumbersProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Add Two Numbers")
                .slug("add-two-numbers")
                .description("You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.\n\n" +
                        "You may assume the two numbers do not contain any leading zero, except the number 0 itself.\n\n" +
                        "**Example:**\n```\nInput: l1 = [2,4,3], l2 = [5,6,4]\nOutput: [7,0,8]\nExplanation: 342 + 465 = 807.\n```")
                .difficulty(Difficulty.MEDIUM)
                .timeLimit(1.5)
                .memoryLimit(256)
                .tags(new HashSet<>(Arrays.asList(tags.get("linked-list"), tags.get("math"))))
                .hints(Arrays.asList(
                        "Remember to handle the carry value",
                        "Process both lists node by node"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("[2,4,3]\n[5,6,4]")
                .expectedOutput("[7,0,8]")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[0]\n[0]")
                .expectedOutput("[0]")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def addTwoNumbers(self, l1: ListNode, l2: ListNode) -> ListNode:\n        # Your code here\n        pass",
            "var addTwoNumbers = function(l1, l2) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    ListNode* addTwoNumbers(ListNode* l1, ListNode* l2) {\n        // Your code here\n    }\n};",
            "func addTwoNumbers(l1 *ListNode, l2 *ListNode) *ListNode {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createLongestSubstringProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Longest Substring Without Repeating Characters")
                .slug("longest-substring-without-repeating-characters")
                .description("Given a string `s`, find the length of the longest substring without repeating characters.\n\n" +
                        "**Example 1:**\n```\nInput: s = \"abcabcbb\"\nOutput: 3\nExplanation: The answer is \"abc\", with the length of 3.\n```")
                .difficulty(Difficulty.MEDIUM)
                .timeLimit(1.5)
                .memoryLimit(256)
                .tags(new HashSet<>(Arrays.asList(tags.get("string"), tags.get("hash-table"), tags.get("two-pointers"))))
                .hints(Arrays.asList(
                        "Use a sliding window approach",
                        "Keep track of character positions in a hash map"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("abcabcbb")
                .expectedOutput("3")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("bbbbb")
                .expectedOutput("1")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("pwwkew")
                .expectedOutput("3")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public int lengthOfLongestSubstring(String s) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def lengthOfLongestSubstring(self, s: str) -> int:\n        # Your code here\n        pass",
            "var lengthOfLongestSubstring = function(s) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    int lengthOfLongestSubstring(string s) {\n        // Your code here\n    }\n};",
            "func lengthOfLongestSubstring(s string) int {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createMedianArraysProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Median of Two Sorted Arrays")
                .slug("median-of-two-sorted-arrays")
                .description("Given two sorted arrays `nums1` and `nums2` of size `m` and `n` respectively, return the median of the two sorted arrays.\n\n" +
                        "The overall run time complexity should be O(log (m+n)).\n\n" +
                        "**Example:**\n```\nInput: nums1 = [1,3], nums2 = [2]\nOutput: 2.00000\nExplanation: merged array = [1,2,3] and median is 2.\n```")
                .difficulty(Difficulty.HARD)
                .timeLimit(2.0)
                .memoryLimit(512)
                .tags(new HashSet<>(Arrays.asList(tags.get("array"), tags.get("binary-search"))))
                .hints(Arrays.asList(
                        "Use binary search on the smaller array",
                        "Think about partitioning both arrays"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("[1,3]\n[2]")
                .expectedOutput("2.0")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[1,2]\n[3,4]")
                .expectedOutput("2.5")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public double findMedianSortedArrays(int[] nums1, int[] nums2) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def findMedianSortedArrays(self, nums1: List[int], nums2: List[int]) -> float:\n        # Your code here\n        pass",
            "var findMedianSortedArrays = function(nums1, nums2) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    double findMedianSortedArrays(vector<int>& nums1, vector<int>& nums2) {\n        // Your code here\n    }\n};",
            "func findMedianSortedArrays(nums1 []int, nums2 []int) float64 {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createPalindromeNumberProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Palindrome Number")
                .slug("palindrome-number")
                .description("Given an integer `x`, return `true` if `x` is a palindrome, and `false` otherwise.\n\n" +
                        "**Example 1:**\n```\nInput: x = 121\nOutput: true\nExplanation: 121 reads as 121 from left to right and from right to left.\n```")
                .difficulty(Difficulty.EASY)
                .timeLimit(1.0)
                .memoryLimit(128)
                .tags(new HashSet<>(Arrays.asList(tags.get("math"))))
                .hints(Arrays.asList(
                        "Could you solve it without converting the integer to a string?",
                        "Think about reversing the number"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("121")
                .expectedOutput("true")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("-121")
                .expectedOutput("false")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("10")
                .expectedOutput("false")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public boolean isPalindrome(int x) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def isPalindrome(self, x: int) -> bool:\n        # Your code here\n        pass",
            "var isPalindrome = function(x) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    bool isPalindrome(int x) {\n        // Your code here\n    }\n};",
            "func isPalindrome(x int) bool {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createValidParenthesesProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Valid Parentheses")
                .slug("valid-parentheses")
                .description("Given a string `s` containing just the characters `'('`, `')'`, `'{'`, `'}'`, `'['` and `']'`, determine if the input string is valid.\n\n" +
                        "An input string is valid if:\n" +
                        "1. Open brackets must be closed by the same type of brackets.\n" +
                        "2. Open brackets must be closed in the correct order.\n\n" +
                        "**Example:**\n```\nInput: s = \"()\"\nOutput: true\n```")
                .difficulty(Difficulty.EASY)
                .timeLimit(1.0)
                .memoryLimit(128)
                .tags(new HashSet<>(Arrays.asList(tags.get("string"), tags.get("stack"))))
                .hints(Arrays.asList(
                        "Use a stack to keep track of opening brackets",
                        "When you encounter a closing bracket, check if it matches the most recent opening bracket"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("()")
                .expectedOutput("true")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("()[]{}")
                .expectedOutput("true")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("(]")
                .expectedOutput("false")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public boolean isValid(String s) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def isValid(self, s: str) -> bool:\n        # Your code here\n        pass",
            "var isValid = function(s) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    bool isValid(string s) {\n        // Your code here\n    }\n};",
            "func isValid(s string) bool {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createMergeSortedListsProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Merge Two Sorted Lists")
                .slug("merge-two-sorted-lists")
                .description("You are given the heads of two sorted linked lists `list1` and `list2`.\n\n" +
                        "Merge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists.\n\n" +
                        "Return the head of the merged linked list.\n\n" +
                        "**Example:**\n```\nInput: list1 = [1,2,4], list2 = [1,3,4]\nOutput: [1,1,2,3,4,4]\n```")
                .difficulty(Difficulty.EASY)
                .timeLimit(1.0)
                .memoryLimit(128)
                .tags(new HashSet<>(Arrays.asList(tags.get("linked-list"))))
                .hints(Arrays.asList(
                        "Use a dummy node to simplify the merge logic",
                        "Compare the values of the current nodes in both lists"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("[1,2,4]\n[1,3,4]")
                .expectedOutput("[1,1,2,3,4,4]")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[]\n[]")
                .expectedOutput("[]")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def mergeTwoLists(self, list1: ListNode, list2: ListNode) -> ListNode:\n        # Your code here\n        pass",
            "var mergeTwoLists = function(list1, list2) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    ListNode* mergeTwoLists(ListNode* list1, ListNode* list2) {\n        // Your code here\n    }\n};",
            "func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {\n    // Your code here\n}"
        );

        return problem;
    }

    private Problem createMaxSubarrayProblem(Map<String, Tag> tags) {
        Problem problem = Problem.builder()
                .title("Maximum Subarray")
                .slug("maximum-subarray")
                .description("Given an integer array `nums`, find the subarray with the largest sum, and return its sum.\n\n" +
                        "**Example:**\n```\nInput: nums = [-2,1,-3,4,-1,2,1,-5,4]\nOutput: 6\nExplanation: The subarray [4,-1,2,1] has the largest sum 6.\n```")
                .difficulty(Difficulty.MEDIUM)
                .timeLimit(1.5)
                .memoryLimit(256)
                .tags(new HashSet<>(Arrays.asList(tags.get("array"), tags.get("dynamic-programming"))))
                .hints(Arrays.asList(
                        "Use Kadane's Algorithm",
                        "Keep track of the maximum sum ending at each position"
                ))
                .build();

        problem.addTestCase(TestCase.builder()
                .input("[-2,1,-3,4,-1,2,1,-5,4]")
                .expectedOutput("6")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[1]")
                .expectedOutput("1")
                .isPublic(true)
                .build());

        problem.addTestCase(TestCase.builder()
                .input("[5,4,-1,7,8]")
                .expectedOutput("23")
                .isPublic(false)
                .build());

        addCodeTemplates(problem,
            "class Solution {\n    public int maxSubArray(int[] nums) {\n        // Your code here\n    }\n}",
            "class Solution:\n    def maxSubArray(self, nums: List[int]) -> int:\n        # Your code here\n        pass",
            "var maxSubArray = function(nums) {\n    // Your code here\n};",
            "class Solution {\npublic:\n    int maxSubArray(vector<int>& nums) {\n        // Your code here\n    }\n};",
            "func maxSubArray(nums []int) int {\n    // Your code here\n}"
        );

        return problem;
    }

    private void addCodeTemplates(Problem problem, String java, String python, String javascript, String cpp, String go) {
        problem.addTemplate(CodeTemplate.builder()
                .language("JAVA")
                .codeBoilerplate(java)
                .problem(problem)
                .build());

        problem.addTemplate(CodeTemplate.builder()
                .language("PYTHON")
                .codeBoilerplate(python)
                .problem(problem)
                .build());

        problem.addTemplate(CodeTemplate.builder()
                .language("JAVASCRIPT")
                .codeBoilerplate(javascript)
                .problem(problem)
                .build());

        problem.addTemplate(CodeTemplate.builder()
                .language("CPP")
                .codeBoilerplate(cpp)
                .problem(problem)
                .build());

        problem.addTemplate(CodeTemplate.builder()
                .language("GO")
                .codeBoilerplate(go)
                .problem(problem)
                .build());
    }
}

