package Schmooze.Media.New.Idea.Analyzer.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IdeaReport {

    private String originalIdea;

    private String problemSummary;

    private String targetCustomer;

    private String marketAnalysis;

    private String riskLevel;

    private int profitabilityScore;

    private String competitors;

    private String techStack;

    private LocalDateTime createdAt = LocalDateTime.now();
}