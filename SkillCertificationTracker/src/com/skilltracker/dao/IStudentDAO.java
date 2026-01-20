package com.skilltracker.dao;

import com.skilltracker.model.Student;
import com.skilltracker.exception.DataNotFoundException;
import java.util.List;

public interface IStudentDAO {

    void addStudent(Student student);

    Student getStudentById(int id) throws DataNotFoundException;

    List<Student> getAllStudents();
}
