package com.leetcode.judgeservice.infrastructure.execution;

import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.domain.exception.CodeExecutionException;
import com.leetcode.judgeservice.infrastructure.execution.strategy.ILanguagesStrategy;
import com.leetcode.judgeservice.infrastructure.execution.strategy.PythonStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("DockerExecutionEngine Unit Tests")
class DockerExecutionEngineTest {

    private DockerExecutionEngine executionEngine;

    @BeforeEach
    void setUp() {
        List<ILanguagesStrategy> strategies = List.of(
                new PythonStrategy()
                // Add other strategies as needed for comprehensive testing
        );
        executionEngine = new DockerExecutionEngine(strategies);
    }

    @Test
    @DisplayName("Should execute simple Python code successfully")
    void shouldExecuteSimplePythonCodeSuccessfully() {
        // Given
        String userCode = "print('Hello, World!')";
        String language = "python";
        String input = "";
        String expectedOutput = "Hello, World!";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getOutput()).isEqualTo(expectedOutput);
        assertThat(result.getExpectedOutput()).isEqualTo(expectedOutput);
        assertThat(result.getExecutionTime()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Should fail execution with wrong output")
    void shouldFailExecutionWithWrongOutput() {
        // Given
        String userCode = "print('Wrong Output')";
        String language = "python";
        String input = "";
        String expectedOutput = "Hello, World!";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getOutput()).isEqualTo("Wrong Output");
    }

    @Test
    @DisplayName("Should handle runtime errors")
    void shouldHandleRuntimeErrors() {
        // Given
        String userCode = "print(undefined_variable)";
        String language = "python";
        String input = "";
        String expectedOutput = "Some Output";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getOutput()).contains("Runtime Error");
    }

    @Test
    @DisplayName("Should throw exception for invalid language")
    void shouldThrowExceptionForInvalidLanguage() {
        // Given
        String userCode = "print('Hello')";
        String language = "invalid_language";
        String input = "";
        String expectedOutput = "Hello";

        // When & Then
        assertThatThrownBy(() -> executionEngine.executeCode(userCode, language, input, expectedOutput))
                .isInstanceOf(CodeExecutionException.class)
                .hasMessageContaining("Invalid language format");
    }

    @Test
    @DisplayName("Should handle code with arithmetic operations")
    void shouldHandleCodeWithArithmeticOperations() {
        // Given
        String userCode = "result = 5 + 3\nprint(result)";
        String language = "python";
        String input = "";
        String expectedOutput = "8";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getOutput()).isEqualTo(expectedOutput);
    }

    @Test
    @DisplayName("Should handle multiline output")
    void shouldHandleMultilineOutput() {
        // Given
        String userCode = "print('Line 1')\nprint('Line 2')\nprint('Line 3')";
        String language = "python";
        String input = "";
        String expectedOutput = "Line 1\nLine 2\nLine 3";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
    }

    @Test
    @DisplayName("Should handle syntax errors")
    void shouldHandleSyntaxErrors() {
        // Given
        String userCode = "print('missing closing quote)";
        String language = "python";
        String input = "";
        String expectedOutput = "Some Output";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getOutput()).contains("Error");
    }

    @Test
    @DisplayName("Should handle empty code")
    void shouldHandleEmptyCode() {
        // Given
        String userCode = "";
        String language = "python";
        String input = "";
        String expectedOutput = "";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        // Result depends on how empty code is handled
    }

    @Test
    @DisplayName("Should trim whitespace when comparing output")
    void shouldTrimWhitespaceWhenComparingOutput() {
        // Given
        String userCode = "print('  Hello  ')";
        String language = "python";
        String input = "";
        String expectedOutput = "  Hello  ";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
    }

    @Test
    @DisplayName("Should execute code with list operations")
    void shouldExecuteCodeWithListOperations() {
        // Given
        String userCode = "numbers = [1, 2, 3, 4, 5]\nprint(sum(numbers))";
        String language = "python";
        String input = "";
        String expectedOutput = "15";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getOutput()).isEqualTo(expectedOutput);
    }

    @Test
    @DisplayName("Should treat bracketed outputs with spacing differences as equal")
    void shouldTreatBracketedOutputsWithSpacingDifferencesAsEqual() {
        // Given
        String userCode = "print([0, 1])";
        String language = "python";
        String input = "";
        String expectedOutput = "[0,1]";

        // When
        SubmissionResult result = executionEngine.executeCode(userCode, language, input, expectedOutput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getOutput()).isEqualTo("[0, 1]");
        assertThat(result.getExpectedOutput()).isEqualTo(expectedOutput);
    }
}

