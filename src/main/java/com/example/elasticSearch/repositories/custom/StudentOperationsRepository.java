package com.example.elasticSearch.repositories.custom;

import com.example.elasticSearch.models.Grade;
import com.example.elasticSearch.models.Student;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentOperationsRepository {

    List<Student> findStudentsWithEnrollmentsMoreThan(int enrollmentsCount, Pageable pageable);

    List<Student> findStudentsWithEnrollmentsCountBetween(int minCount, int maxCount, Pageable pageable);

    List<Student> findStudentsEnrolledInCourse(String CourseName, Pageable pageable);

    List<Student> findStudentsWithGradeEqualTo(Grade grade, Pageable pageable);

    List<Student> findStudentsWithGradeEqualToInCourse(Grade grade, String courseName, Pageable pageable);
}
