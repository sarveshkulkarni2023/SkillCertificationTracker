package com.skilltracker.dao;

import com.skilltracker.model.Certification;
import com.skilltracker.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CertificationDAO {

    public void addCertification(Certification cert) throws SQLException {

        String sql =
          "INSERT INTO certifications(student_id, skill_id, issue_date, expiry_date) " +
          "VALUES (?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cert.getStudentId());
            ps.setInt(2, cert.getSkillId());
            ps.setDate(3, Date.valueOf(cert.getIssueDate()));
            ps.setDate(4, Date.valueOf(cert.getExpiryDate()));
            ps.executeUpdate();
        }
    }
    
    public void viewSkillsByStudent(int studentId) throws SQLException {

        String sql =
          "SELECT s.skill_name, c.issue_date, c.expiry_date " +
          "FROM certifications c " +
          "JOIN skills s ON c.skill_id = s.skill_id " +
          "WHERE c.student_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- Skills ---");
                while (rs.next()) {
                    System.out.println(
                        rs.getString("skill_name") +
                        " | Issued: " + rs.getDate("issue_date") +
                        " | Expiry: " + rs.getDate("expiry_date")
                    );
                }
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

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("Certification not found");
            } else {
                System.out.println("Certification updated");
            }
        }
    }


}
