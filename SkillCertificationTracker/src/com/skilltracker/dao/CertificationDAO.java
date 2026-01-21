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
            ps.setDate(5,
                cert.getExpiryDate() != null
                    ? Date.valueOf(cert.getExpiryDate())
                    : null
            );

            ps.executeUpdate();
        }
    }

    // ---------------- VIEW ALL STUDENTS ----------------
    public void viewAllStudentDetails() throws SQLException {

        String sql =
            "SELECT st.student_id, st.name, " +
            "GROUP_CONCAT(DISTINCT s.skill_name ORDER BY s.skill_name SEPARATOR ', ') AS skills, " +
            "GROUP_CONCAT(DISTINCT c.certificate_name SEPARATOR ', ') AS certificates " +
            "FROM students st " +
            "LEFT JOIN student_skills ss ON st.student_id = ss.student_id " +
            "LEFT JOIN skills s ON ss.skill_id = s.skill_id " +
            "LEFT JOIN certifications c ON st.student_id = c.student_id " +
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
            "LEFT JOIN student_skills ss ON st.student_id = ss.student_id " +
            "LEFT JOIN skills s ON ss.skill_id = s.skill_id " +
            "LEFT JOIN certifications c ON st.student_id = c.student_id " +
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

                System.out.printf(
                    "%-4s %-15s %-25s %-25s %-30s %-12s%n",
                    "ID", "NAME", "EMAIL", "SKILLS", "CERTIFICATION", "EXPIRY"
                );

                do {
                    System.out.printf(
                        "%-4d %-15s %-25s %-25s %-30s %-12s%n",
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("skills"),
                        rs.getString("certificate_name"),
                        rs.getDate("expiry_date")
                    );
                } while (rs.next());
            }
        }
    }

    // ---------------- UPDATE EXPIRY ----------------
    public void updateCertificationExpiry(
            int studentId, String certificateName, LocalDate newExpiry)
            throws SQLException {

        String sql =
            "UPDATE certifications SET expiry_date=? " +
            "WHERE student_id=? AND certificate_name=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1,
                newExpiry != null ? Date.valueOf(newExpiry) : null
            );
            ps.setInt(2, studentId);
            ps.setString(3, certificateName);
            ps.executeUpdate();
        }
    }

    // ---------------- EXPIRED CERTIFICATES ----------------
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

    // ---------------- ADD SKILLS TO STUDENT ----------------
    public void addSkillsToStudent(Integer id, String name, List<String> skills)
            throws SQLException {

        String insert =
            "INSERT IGNORE INTO student_skills (student_id, skill_id) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection()) {

            Integer studentId = getStudentId(con, id, name);
            if (studentId == null) {
                System.out.println("Student not found");
                return;
            }

            for (String skill : skills) {
                int skillId = getOrCreateSkill(con, skill);

                try (PreparedStatement ps = con.prepareStatement(insert)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, skillId);
                    ps.executeUpdate();
                }
            }

            System.out.println("Skills added successfully");
        }
    }

    // ---------------- ADD CERTIFICATES TO STUDENT ----------------
    public void addCertificatesToStudent(
            Integer id, String name,
            List<Certification> certs) throws SQLException {

        String insert =
            "INSERT INTO certifications " +
            "(student_id, skill_id, certificate_name, issue_date, expiry_date) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            Integer studentId = getStudentId(con, id, name);
            if (studentId == null) {
                System.out.println("Student not found");
                return;
            }

            for (Certification cert : certs) {

                int skillId = getOrCreateSkill(con, cert.getSkillName());

                // ensure student-skill mapping
                try (PreparedStatement ps =
                         con.prepareStatement(
                             "INSERT IGNORE INTO student_skills VALUES (?, ?)")) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, skillId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(insert)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, skillId);
                    ps.setString(3, cert.getCertificateName());
                    ps.setDate(4, Date.valueOf(LocalDate.now()));
                    ps.setDate(5,
                        cert.getExpiryDate() != null
                            ? Date.valueOf(cert.getExpiryDate())
                            : null
                    );
                    ps.executeUpdate();
                }
            }

            con.commit();
            System.out.println("Certificates added successfully");
        }
    }

    
    public void deleteByIdOrName(Integer id, String name) throws SQLException {

        String selectStudent =
            "SELECT student_id FROM students WHERE student_id = ? OR name = ?";

        String deleteCert =
            "DELETE FROM certifications WHERE student_id = ?";

        String deleteStudentSkills =
            "DELETE FROM student_skills WHERE student_id = ?";

        String deleteStudent =
            "DELETE FROM students WHERE student_id = ?";

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            Integer studentId = null;

            // 1️⃣ Find student
            try (PreparedStatement ps = con.prepareStatement(selectStudent)) {
                ps.setObject(1, id);
                ps.setObject(2, name);

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

            // 3️⃣ Delete student_skills
            try (PreparedStatement ps = con.prepareStatement(deleteStudentSkills)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            // 4️⃣ Delete student
            try (PreparedStatement ps = con.prepareStatement(deleteStudent)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("Student record deleted successfully");

        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    
    // ---------------- HELPER METHODS ----------------
    private Integer getStudentId(Connection con, Integer id, String name)
            throws SQLException {

        String sql =
            "SELECT student_id FROM students WHERE student_id = ? OR name = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.setObject(2, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("student_id");
            }
        }
        return null;
    }

    private int getOrCreateSkill(Connection con, String skillName)
            throws SQLException {

        String select = "SELECT skill_id FROM skills WHERE skill_name = ?";
        String insert = "INSERT INTO skills(skill_name) VALUES(?)";

        try (PreparedStatement ps = con.prepareStatement(select)) {
            ps.setString(1, skillName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("skill_id");
            }
        }

        try (PreparedStatement ps =
                 con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, skillName);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
