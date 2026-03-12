package com.leetcode.judgeservice.application.service;

import com.leetcode.judgeservice.application.mapper.SubmissionMapper;
import com.leetcode.judgeservice.domain.entity.Submission;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.domain.enums.SubmissionStatus;
import com.leetcode.judgeservice.domain.exception.JudgeServiceException;
import com.leetcode.judgeservice.infrastructure.client.dto.ProblemDetailResponse;
import com.leetcode.judgeservice.infrastructure.client.dto.TestCaseDto;
import com.leetcode.judgeservice.infrastructure.client.feign.ProblemFeignClient;
import com.leetcode.judgeservice.infrastructure.repository.SubmissionRepository;
import com.leetcode.judgeservice.presentation.dto.SubmissionRequest;
import com.leetcode.judgeservice.presentation.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository repository;
    private final ProblemFeignClient problemFeignClient;
    private final ICodeExecutionEngine engine;
    private final SubmissionMapper mapper;

    @Transactional
    public SubmissionResponse submit(UUID userId, SubmissionRequest request) {

        Submission submission = mapper.toEntity(request);
        submission.setUserID(userId);
        submission = repository.save(submission);


        ProblemDetailResponse problem = problemFeignClient.getProblem(request.problemSlug());

        List<TestCaseDto> testCases = problem.testCases();
        if (testCases == null || testCases.isEmpty()) {
            throw new JudgeServiceException("No test cases found for problem: " + request.problemSlug());
        }

        boolean allPassed = true;

        for(TestCaseDto testCase : testCases) {

            SubmissionResult result = engine.executeCode(
                    request.code(),
                    request.language(),
                    testCase.input(),
                    testCase.expectedOutput()
            );

            submission.addResult(result);

            if(!result.isPassed()) {
                allPassed = false;
            }
        }

        submission.setStatus(allPassed ? SubmissionStatus.ACCEPTED : SubmissionStatus.WRONG_ANSWER);
        submission.setCompletedAt(LocalDateTime.now());

        return mapper.toResponse(repository.save(submission));
    }

    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getUserSubmissions(UUID userId, Pageable pageable) {
        return repository.findByUserID(userId, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getUserSubmissionsForProblem(UUID userId, String problemSlug) {
        return repository.findByUserIDAndProblemSlug(userId, problemSlug)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(UUID submissionId, UUID userId) {
        Submission submission = repository.findById(submissionId)
                .orElseThrow(() -> new JudgeServiceException("Submission not found: " + submissionId));

        // Vérifier que la soumission appartient à l'utilisateur
        if (!submission.getUserID().equals(userId)) {
            throw new JudgeServiceException("Unauthorized access to submission");
        }

        return mapper.toResponse(submission);
    }
}
