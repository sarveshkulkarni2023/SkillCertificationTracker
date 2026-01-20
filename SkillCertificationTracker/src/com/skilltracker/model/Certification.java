package com.skilltracker.model;

import java.time.LocalDate;

public class Certification {

    private int certId;
    private int studentId;
    private int skillId;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    public Certification(int studentId, int skillId,
                          LocalDate issueDate, LocalDate expiryDate) {
        this.studentId = studentId;
        this.skillId = skillId;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSkillId() {
        return skillId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
