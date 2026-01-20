package com.skilltracker.dao;

import com.skilltracker.model.Student;
import com.skilltracker.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    public void addStudent(Student student) throws SQLException {
        String sql =
            "INSERT INTO students(name, email) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.executeUpdate();
        }
    }
    
    public void viewAllStudents() throws SQLException {

        String sql = "SELECT student_id, name, email FROM students";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Students ---");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("student_id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("email")
                );
            }
        }
    }
    
    public void updateStudent(int id, String name, String email)
            throws SQLException {

        String sql =
            "UPDATE students SET name=?, email=? WHERE student_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("Student not found");
            } else {
                System.out.println("Student updated");
            }
        }
    }


}
