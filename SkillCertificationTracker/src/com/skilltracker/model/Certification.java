package com.skilltracker.model;

import java.time.LocalDate;

public class Certification {

    private int studentId;
    private int skillId;
    private String skillName;          // ✅ needed
    private String certificateName;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    // ---------- CONSTRUCTORS ----------

    public Certification() {
    }

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

    // ---------- GETTERS & SETTERS ----------

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
