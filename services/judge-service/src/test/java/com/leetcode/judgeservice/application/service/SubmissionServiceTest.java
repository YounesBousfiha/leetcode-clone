package com.leetcode.judgeservice.application.service;

import com.leetcode.judgeservice.application.mapper.SubmissionMapper;
import com.leetcode.judgeservice.domain.entity.Submission;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import com.leetcode.judgeservice.domain.enums.SubmissionStatus;
import com.leetcode.judgeservice.domain.exception.JudgeServiceException;
import com.leetcode.judgeservice.infrastructure.client.dto.ProblemDetailResponse;
import com.leetcode.judgeservice.infrastructure.client.dto.TestCaseDto;
import com.leetcode.judgeservice.infrastructure.client.feign.ProblemFeignClient;
import com.leetcode.judgeservice.infrastructure.repository.SubmissionRepository;
import com.leetcode.judgeservice.presentation.dto.SubmissionRequest;
import com.leetcode.judgeservice.presentation.dto.SubmissionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository repository;

    @Mock
    private ProblemFeignClient problemFeignClient;

    @Mock
    private ICodeExecutionEngine engine;

    @Mock
    private SubmissionMapper mapper;

    @InjectMocks
    private SubmissionService submissionService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("submit - should execute code and return ACCEPTED when all tests pass")
    void submit_shouldReturnAccepted() {
        SubmissionRequest request = SubmissionRequest.builder()
                .code("print('hello')").language("PYTHON").problemId("two-sum").build();
        Submission submission = Submission.builder()
                .id(UUID.randomUUID()).userID(userId).problemId("two-sum")
                .code("print('hello')").language(ProgrammingLanguage.PYTHON)
                .status(SubmissionStatus.PENDING).build();
        ProblemDetailResponse problem = new ProblemDetailResponse(
                "p1", "two-sum", 2.0, 256,
                List.of(new TestCaseDto("[2,7,11]", "[0,1]")));
        SubmissionResult passedResult = SubmissionResult.builder()
                .passed(true).output("[0,1]").expectedOutput("[0,1]").build();
        SubmissionResponse expectedResponse = SubmissionResponse.builder()
                .id(submission.getId()).status("ACCEPTED").build();

        when(mapper.toEntity(request)).thenReturn(submission);
        when(repository.save(any(Submission.class))).thenReturn(submission);
        when(problemFeignClient.getProblem("two-sum")).thenReturn(problem);
        when(engine.executeCode(anyString(), anyString(), anyString(), anyString())).thenReturn(passedResult);
        when(mapper.toResponse(any(Submission.class))).thenReturn(expectedResponse);

        SubmissionResponse result = submissionService.submit(userId, request);

        assertThat(result.status()).isEqualTo("ACCEPTED");
        verify(repository, times(2)).save(any(Submission.class));
    }

    @Test
    @DisplayName("submit - should return WRONG_ANSWER when test fails")
    void submit_shouldReturnWrongAnswer() {
        SubmissionRequest request = SubmissionRequest.builder()
                .code("code").language("JAVA").problemId("two-sum").build();
        Submission submission = Submission.builder()
                .id(UUID.randomUUID()).userID(userId).problemId("two-sum")
                .code("code").language(ProgrammingLanguage.JAVA)
                .status(SubmissionStatus.PENDING).build();
        ProblemDetailResponse problem = new ProblemDetailResponse(
                "p1", "two-sum", 2.0, 256,
                List.of(new TestCaseDto("[2,7]", "[0,1]")));
        SubmissionResult failedResult = SubmissionResult.builder()
                .passed(false).output("[1,0]").expectedOutput("[0,1]").build();
        SubmissionResponse expectedResponse = SubmissionResponse.builder()
                .id(submission.getId()).status("WRONG_ANSWER").build();

        when(mapper.toEntity(request)).thenReturn(submission);
        when(repository.save(any(Submission.class))).thenReturn(submission);
        when(problemFeignClient.getProblem("two-sum")).thenReturn(problem);
        when(engine.executeCode(anyString(), anyString(), anyString(), anyString())).thenReturn(failedResult);
        when(mapper.toResponse(any(Submission.class))).thenReturn(expectedResponse);

        SubmissionResponse result = submissionService.submit(userId, request);

        assertThat(result.status()).isEqualTo("WRONG_ANSWER");
    }

    @Test
    @DisplayName("getUserSubmissions - should return paginated submissions")
    void getUserSubmissions_shouldReturnPaginated() {
        Pageable pageable = PageRequest.of(0, 20);
        Submission submission = Submission.builder()
                .id(UUID.randomUUID()).userID(userId).status(SubmissionStatus.ACCEPTED).build();
        SubmissionResponse response = SubmissionResponse.builder()
                .id(submission.getId()).status("ACCEPTED").build();
        Page<Submission> page = new PageImpl<>(List.of(submission), pageable, 1);

        when(repository.findByUserID(userId, pageable)).thenReturn(page);
        when(mapper.toResponse(submission)).thenReturn(response);

        Page<SubmissionResponse> result = submissionService.getUserSubmissions(userId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).status()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("getUserSubmissionsForProblem - should return submissions for specific problem")
    void getUserSubmissionsForProblem_shouldReturnForProblem() {
        Submission submission = Submission.builder()
                .id(UUID.randomUUID()).userID(userId).problemId("two-sum")
                .status(SubmissionStatus.ACCEPTED).build();
        SubmissionResponse response = SubmissionResponse.builder()
                .id(submission.getId()).status("ACCEPTED").build();

        when(repository.findByUserIDAndProblemId(userId, "two-sum")).thenReturn(List.of(submission));
        when(mapper.toResponse(submission)).thenReturn(response);

        List<SubmissionResponse> result = submissionService.getUserSubmissionsForProblem(userId, "two-sum");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getSubmissionById - should return submission when owned by user")
    void getSubmissionById_shouldReturnWhenOwned() {
        UUID submissionId = UUID.randomUUID();
        Submission submission = Submission.builder()
                .id(submissionId).userID(userId).status(SubmissionStatus.ACCEPTED).build();
        SubmissionResponse response = SubmissionResponse.builder()
                .id(submissionId).status("ACCEPTED").build();

        when(repository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(mapper.toResponse(submission)).thenReturn(response);

        SubmissionResponse result = submissionService.getSubmissionById(submissionId, userId);

        assertThat(result.id()).isEqualTo(submissionId);
    }

    @Test
    @DisplayName("getSubmissionById - should throw when submission not found")
    void getSubmissionById_shouldThrowWhenNotFound() {
        UUID submissionId = UUID.randomUUID();
        when(repository.findById(submissionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.getSubmissionById(submissionId, userId))
                .isInstanceOf(JudgeServiceException.class)
                .hasMessageContaining("Submission not found");
    }

    @Test
    @DisplayName("getSubmissionById - should throw when user unauthorized")
    void getSubmissionById_shouldThrowWhenUnauthorized() {
        UUID submissionId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Submission submission = Submission.builder()
                .id(submissionId).userID(otherUserId).status(SubmissionStatus.ACCEPTED).build();

        when(repository.findById(submissionId)).thenReturn(Optional.of(submission));

        assertThatThrownBy(() -> submissionService.getSubmissionById(submissionId, userId))
                .isInstanceOf(JudgeServiceException.class)
                .hasMessageContaining("Unauthorized");
    }
}

