package com.leetcode.judgeservice.domain.entity;


import com.leetcode.judgeservice.domain.enums.ProgrammingLanguage;
import com.leetcode.judgeservice.domain.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userID;

    @Column(nullable = false)
    private String problemId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    private Double executionTime;
    private long memoryUsed;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmissionResult> resultList = new ArrayList<>();

    public void addResult(SubmissionResult result) {
        resultList.add(result);
        result.setSubmission(this);
    }
}
