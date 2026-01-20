package com.skilltracker.dao;

import com.skilltracker.model.Certification;
import com.skilltracker.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;

public class CertificationDAO {

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
            ps.setDate(5, Date.valueOf(cert.getExpiryDate()));

            ps.executeUpdate();
        }
    }

    // 🔥 AGGREGATED VIEW (NO DUPLICATES)
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

    public void updateCertificationExpiry(
            int studentId, int skillId, LocalDate newExpiry)
            throws SQLException {

        String sql =
          "UPDATE certifications SET expiry_date=? " +
          "WHERE student_id=? AND skill_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newExpiry));
            ps.setInt(2, studentId);
            ps.setInt(3, skillId);
            ps.executeUpdate();
        }
    }
}
