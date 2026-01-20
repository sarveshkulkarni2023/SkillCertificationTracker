package com.skilltracker.service;

import com.skilltracker.dao.IStudentDAO;
import com.skilltracker.dao.StudentDAOImpl;
import com.skilltracker.model.Student;
import com.skilltracker.exception.DataNotFoundException;

public class StudentServiceImpl implements StudentService {

    private IStudentDAO studentDAO = new StudentDAOImpl();

    @Override
    public void registerStudent(Student student) {
        studentDAO.addStudent(student);
    }

    @Override
    public Student findStudent(int id) throws DataNotFoundException {
        return studentDAO.getStudentById(id);
    }
}
