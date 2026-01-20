package com.skilltracker.dao;

import com.skilltracker.model.Certification;
import com.skilltracker.model.ExpiredCertificateView;
import com.skilltracker.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CertificationDAO implements ICertificationDAO {

    // ---------------- ADD CERTIFICATION ----------------
    @Override
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

    // ---------------- VIEW ALL STUDENTS ----------------
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

    // ---------------- FIND STUDENT ----------------
    public void findStudent(String input) throws SQLException {

        String sql =
            "SELECT st.student_id, st.name, st.email, " +
            "GROUP_CONCAT(DISTINCT s.skill_name ORDER BY s.skill_name SEPARATOR ', ') AS skills, " +
            "c.certificate_name, c.expiry_date " +
            "FROM students st " +
            "LEFT JOIN certifications c ON st.student_id = c.student_id " +
            "LEFT JOIN skills s ON c.skill_id = s.skill_id " +
            "WHERE st.student_id = ? OR st.name LIKE ? " +
            "GROUP BY st.student_id, st.name, st.email, c.certificate_name, c.expiry_date " +
            "ORDER BY st.student_id, c.expiry_date";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int id = -1;
            try { id = Integer.parseInt(input); } catch (Exception ignored) {}

            ps.setInt(1, id);
            ps.setString(2, "%" + input + "%");

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Student not found");
                    return;
                }

                // HEADER
                System.out.printf(
                	    "%-4s %-12s %-25s %-18s %-35s %-12s%n",
                	    "ID", "NAME", "EMAIL", "SKILL", "CERTIFICATION", "EXPIRY"
                	);


                int lastStudentId = -1;

                do {
                    int currentId = rs.getInt("student_id");

                    String idCol     = (currentId != lastStudentId) ? String.valueOf(currentId) : "";
                    String nameCol   = (currentId != lastStudentId) ? rs.getString("name") : "";
                    String emailCol  = (currentId != lastStudentId) ? rs.getString("email") : "";
                    String skillCol  = (currentId != lastStudentId) ? rs.getString("skills") : "";

                    String certCol   = rs.getString("certificate_name");
                    Date expDate     = rs.getDate("expiry_date");

                    System.out.printf(
                    	    "%-4d %-12s %-25s %-18s %-35s %-12s%n",
                    	    rs.getInt("student_id"),
                    	    rs.getString("name"),
                    	    rs.getString("email"),
                    	    rs.getString("skills"),
                    	    rs.getString("certificate_name"),
                    	    rs.getDate("expiry_date")
                    	);


                    lastStudentId = currentId;

                } while (rs.next());
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

 // ---------------- EXPIRED CERTIFICATES (SERVICE USES THIS) ----------------
    @Override
    public List<ExpiredCertificateView> getExpiredCertificates() throws SQLException {

        String sql =
            "SELECT st.student_id, st.name, s.skill_name, " +
            "c.certificate_name, c.expiry_date " +
            "FROM certifications c " +
            "JOIN students st ON c.student_id = st.student_id " +
            "JOIN skills s ON c.skill_id = s.skill_id " +
            "WHERE c.expiry_date < CURRENT_DATE " +
            "ORDER BY c.expiry_date";

        List<ExpiredCertificateView> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ExpiredCertificateView(
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("skill_name"),
                    rs.getString("certificate_name"),
                    rs.getDate("expiry_date").toLocalDate()
                ));
            }
        }
        return list;
    }
    
    public void deleteByIdOrName(Integer id, String name1) {

        String selectStudent =
            "SELECT student_id FROM students WHERE student_id = ? OR name = ?";

        String deleteCert =
            "DELETE FROM certifications WHERE student_id = ?";

        String deleteStudent =
            "DELETE FROM students WHERE student_id = ?";

        String deleteUnusedSkills =
            "DELETE FROM skills " +
            "WHERE skill_id NOT IN (SELECT DISTINCT skill_id FROM certifications)";

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            Integer studentId = null;

            // 1️⃣ Find student first
            try (PreparedStatement ps = con.prepareStatement(selectStudent)) {
                ps.setObject(1, id);
                ps.setObject(2, name1);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getInt("student_id");
                    }
                }
            }

            if (studentId == null) {
                System.out.println("Student not found");
                return;
            }

            // 2️⃣ Delete certifications
            try (PreparedStatement ps = con.prepareStatement(deleteCert)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            // 3️⃣ Delete student
            int rows;
            try (PreparedStatement ps = con.prepareStatement(deleteStudent)) {
                ps.setInt(1, studentId);
                rows = ps.executeUpdate();
            }

            // 4️⃣ Delete unused skills (safe cleanup)
            try (PreparedStatement ps = con.prepareStatement(deleteUnusedSkills)) {
                ps.executeUpdate();
            }

            con.commit();

            if (rows > 0) {
                System.out.println("Student record deleted successfully");
            }

        } catch (Exception e) {
            try {
                DBUtil.getConnection().rollback();
            } catch (Exception ignored) {}
            System.out.println("Delete failed: " + e.getMessage());
        }
    }




}
