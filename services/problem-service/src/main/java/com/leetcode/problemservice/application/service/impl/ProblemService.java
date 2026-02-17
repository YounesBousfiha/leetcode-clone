package com.leetcode.problemservice.application.service.impl;

import com.leetcode.problemservice.application.mapper.ProblemMapper;
import com.leetcode.problemservice.application.service.IProblemService;
import com.leetcode.problemservice.domain.entity.CodeTemplate;
import com.leetcode.problemservice.domain.entity.Problem;
import com.leetcode.problemservice.domain.entity.Tag;
import com.leetcode.problemservice.domain.entity.TestCase;
import com.leetcode.problemservice.infrastcture.repository.ProblemRepository;
import com.leetcode.problemservice.infrastcture.repository.TagRepository;
import com.leetcode.problemservice.prensentation.dto.CreateProblemRequest;
import com.leetcode.problemservice.prensentation.dto.ProblemDetailResponse;
import com.leetcode.problemservice.prensentation.dto.ProblemListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemService implements IProblemService {

    private final ProblemRepository problemRepository;
    private  final TagRepository tagRepository;
    private final ProblemMapper problemMapper;

    @Override
    public ProblemDetailResponse createProblem(CreateProblemRequest request) {
        log.info("Create new Problem: {}", request.title());

        String slug = request.title().toLowerCase().replace(" ", "-");

        if(problemRepository.existsBySlug(slug)) {
            throw new RuntimeException("Problem with this title already exists");
        }

        Problem problem = problemMapper.toEntity(request);
        problem.setSlug(slug);

        Set<Tag> tags = request.tags().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = Tag.builder()
                                    .name(tagName)
                                    .slug(tagName.toLowerCase().replace(" ", "-"))
                                    .build();
                            return tagRepository.save(newTag);
                        })
                )
                .collect(Collectors.toSet());
        problem.setTags(tags);

        if(request.testCase() != null) {
            request.testCase().forEach(tcDto -> {
                TestCase testCase = TestCase.builder()
                        .input(tcDto.input())
                        .expectedOutput(tcDto.expectedOutput())
                        .isPublic(tcDto.isPublic())
                        .build();
                problem.addTestCase(testCase);
            });
        }

        if(request.codeTemplate() != null) {
            request.codeTemplate().forEach(ctDto -> {
                CodeTemplate template = CodeTemplate.builder()
                        .language(ctDto.language())
                        .codeBoilerplate(ctDto.codeBoilerplate())
                        .build();
                problem.addTemplate(template);
            });
        }

        Problem savedProblem = problemRepository.save(problem);
        log.info("Problem created with ID: {}", savedProblem.getId());

        return problemMapper.toDetailResponse(savedProblem);
    }

    @Override
    public Page<ProblemListResponse> getAllProblems(Pageable pageable) {
        return problemRepository.findAll(pageable)
                .map(problemMapper::toListResponse);
    }

    @Override
    public ProblemDetailResponse getProblemBySlug(String slug) {
        Problem problem = this.problemRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Problem not Found"));

        return problemMapper.toDetailResponse(problem);
    }

    @Override
    public void deleteProblem(String id) {

        UUID uuid = UUID.fromString(id);
        if(!problemRepository.existsById(uuid)) {
            throw new RuntimeException("Problem not found with Id: " + id);
        }

        problemRepository.deleteById(uuid);

        log.info("Problem deleted successfully with id: {}", id);
    }
}
