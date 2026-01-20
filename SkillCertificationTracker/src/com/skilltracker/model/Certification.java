package com.skilltracker.model;

import java.time.LocalDate;

public class Certification {

    private int studentId;
    private int skillId;
    private String certificateName;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    public Certification(int studentId, int skillId,
                         String certificateName,
                         LocalDate issueDate,
                         LocalDate expiryDate) {

        this.studentId = studentId;
        this.skillId = skillId;
        this.certificateName = certificateName;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSkillId() {
        return skillId;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
