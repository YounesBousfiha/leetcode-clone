package com.leetcode.problemservice.domain.entity;


import jakarta.persistence.*;
import lombok.ToString;

import java.util.UUID;

@Entity
public class CodeTemplate {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String language;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String codeBoilerplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    @ToString.Exclude
    private Problem problem;
}
