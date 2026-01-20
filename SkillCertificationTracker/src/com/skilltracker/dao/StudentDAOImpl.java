package com.skilltracker.dao;

import com.skilltracker.model.Student;
import com.skilltracker.exception.DataNotFoundException;
import com.skilltracker.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements IStudentDAO {

    @Override
    public void addStudent(Student student) {
        // existing JDBC logic
    }

    @Override
    public Student getStudentById(int id) throws DataNotFoundException {
        // existing JDBC logic
        throw new DataNotFoundException("Student not found");
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        // loop over ResultSet here
        return students;
    }
}
