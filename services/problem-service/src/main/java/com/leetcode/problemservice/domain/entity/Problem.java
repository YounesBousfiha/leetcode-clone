package com.leetcode.problemservice.domain.entity;


import com.leetcode.problemservice.domain.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private Double timeLimit;

    private Integer memoryLimit;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name= "problem_tags",
            joinColumns = @JoinColumn(name= "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "problem_hints", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "hints")
    @Builder.Default
    private List<String> hints = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CodeTemplate> codeTemplates = new ArrayList<>();

    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
        testCase.setProblem(this);
    }

    public void addTemplate(CodeTemplate template) {
        codeTemplates.add(template);
    }

    public void removeTestCase(TestCase testCase) {
        testCases.remove(testCase);
        testCase.setProblem(null);
    }
}
