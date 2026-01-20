package com.skilltracker.dao;

import com.skilltracker.model.Student;
import com.skilltracker.util.DBUtil;

import java.sql.*;

public class StudentDAO {
 
    public int addStudentAndReturnId(Student student) throws SQLException {

        String sql =
            "INSERT INTO students(name, email) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
    }
}
