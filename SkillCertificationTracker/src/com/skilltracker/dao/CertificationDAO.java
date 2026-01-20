package com.skilltracker.dao;

import com.skilltracker.model.Certification;
import com.skilltracker.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;

public class CertificationDAO {

    // ---------------- ADD CERTIFICATION ----------------
    public void addCertification(Certification cert) throws SQLException {

        String sql =
            "INSERT INTO certifications " +
            "(student_id, skill_id, certificate_name, issue_date, expiry_date) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cert.getStudentId());
            ps.setInt(2, cert.getSkillId());
            ps.setString(3, cert.getCertificateName());
            ps.setDate(4, Date.valueOf(cert.getIssueDate()));

            if (cert.getExpiryDate() != null) {
                ps.setDate(5, Date.valueOf(cert.getExpiryDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.executeUpdate();
        }
    }

    // ---------------- VIEW ALL STUDENTS (NO DUPLICATES) ----------------
    public void viewAllStudentDetails() throws SQLException {

        String sql =
            "SELECT st.student_id, st.name, " +
            "GROUP_CONCAT(DISTINCT s.skill_name SEPARATOR ', ') AS skills, " +
            "GROUP_CONCAT(DISTINCT c.certificate_name SEPARATOR ', ') AS certificates " +
            "FROM students st " +
            "LEFT JOIN certifications c ON st.student_id = c.student_id " +
            "LEFT JOIN skills s ON c.skill_id = s.skill_id " +
            "GROUP BY st.student_id, st.name " +
            "ORDER BY st.student_id";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.printf(
                "%-4s %-20s %-30s %-40s%n",
                "ID", "Name", "Skills", "Certifications"
            );

            while (rs.next()) {
                System.out.printf(
                    "%-4d %-20s %-30s %-40s%n",
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("skills"),
                    rs.getString("certificates")
                );
            }
        }
    }

    // ---------------- FIND STUDENT (BY ID OR NAME) ----------------
    public void findStudent(String input) throws SQLException {

        String sql =
            "SELECT st.student_id, st.name, st.email, " +
            "GROUP_CONCAT(DISTINCT s.skill_name SEPARATOR ', ') AS skills, " +
            "GROUP_CONCAT(DISTINCT c.certificate_name SEPARATOR ', ') AS certificates, " +
            "GROUP_CONCAT(DISTINCT DATE_FORMAT(c.expiry_date, '%Y-%m-%d') SEPARATOR ', ') AS expiry_dates " +
            "FROM students st " +
            "LEFT JOIN certifications c ON st.student_id = c.student_id " +
            "LEFT JOIN skills s ON c.skill_id = s.skill_id " +
            "WHERE st.student_id = ? OR st.name LIKE ? " +
            "GROUP BY st.student_id, st.name, st.email";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int id = -1;
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException ignored) {}

            ps.setInt(1, id);
            ps.setString(2, "%" + input + "%");

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Student not found");
                    return;
                }

                System.out.println("\n--- Student Details ---");
                System.out.println("ID            : " + rs.getInt("student_id"));
                System.out.println("Name          : " + rs.getString("name"));
                System.out.println("Email         : " + rs.getString("email"));
                System.out.println("Skills        : " + rs.getString("skills"));
                System.out.println("Certifications: " + rs.getString("certificates"));
                System.out.println("Expiry Dates  : " + rs.getString("expiry_dates"));
            }
        }
    }

    // ---------------- UPDATE EXPIRY ----------------
    public void updateCertificationExpiry(
            int studentId, int skillId, LocalDate newExpiry)
            throws SQLException {

        String sql =
            "UPDATE certifications SET expiry_date=? " +
            "WHERE student_id=? AND skill_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (newExpiry != null) {
                ps.setDate(1, Date.valueOf(newExpiry));
            } else {
                ps.setNull(1, Types.DATE);
            }

            ps.setInt(2, studentId);
            ps.setInt(3, skillId);
            ps.executeUpdate();
        }
    }
}
