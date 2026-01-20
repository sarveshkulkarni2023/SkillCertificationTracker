package com.skilltracker.service;

import com.skilltracker.model.Student;
import com.skilltracker.exception.DataNotFoundException;

public interface StudentService {

    void registerStudent(Student student);

    Student findStudent(int id) throws DataNotFoundException;
}
