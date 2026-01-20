package com.skilltracker.model;

import java.time.LocalDate;

/**
 * Read-only model for expired certificate reporting.
 */
public class ExpiredCertificateView {

    private int studentId;
    private String studentName;
    private String skillName;
    private String certificateName;
    private LocalDate expiryDate;

    public ExpiredCertificateView(
            int studentId,
            String studentName,
            String skillName,
            String certificateName,
            LocalDate expiryDate) {

        this.studentId = studentId;
        this.studentName = studentName;
        this.skillName = skillName;
        this.certificateName = certificateName;
        this.expiryDate = expiryDate;
    }

    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSkillName() { return skillName; }
    public String getCertificateName() { return certificateName; }
    public LocalDate getExpiryDate() { return expiryDate; }
}
