package com.example.elasticSearch.repositories.custom;

import com.example.elasticSearch.models.Enrollment;
import com.example.elasticSearch.models.Grade;
import com.example.elasticSearch.models.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.elasticSearch.InitialTestData.*;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StudentOperationsRepositoryTest {

    @Autowired
    StudentOperationsRepository studentOperationsRepository;

    @Test
    public void filterStudents_byEnrollmentCountsMoreThan_ShouldPass() {
        // create
        int enrollmentCount = 3;
        int count = (int) getEnrollmentCountsMapStream(enrollments)
                .filter(entry -> entry.getValue() > enrollmentCount)
                .count();

        // test
        List<Student> students = studentOperationsRepository
                .findStudentsWithEnrollmentsMoreThan(enrollmentCount, Pageable.unpaged());

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudents_byEnrollmentCountsBetween_ShouldPass() {
        // create
        int minCount = 3;
        int maxCount = 6;
        int count = (int) getEnrollmentCountsMapStream(enrollments)
                .filter(entry -> entry.getValue() > minCount &&
                        entry.getValue() < maxCount)
                .count();

        // test
        List<Student> students = studentOperationsRepository
                .findStudentsWithEnrollmentsCountBetween(minCount, maxCount, Pageable.unpaged());

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudents_enrolledInCourse_ShouldPass() {
        // create
        String randomCourseName = COURSE_NAMES[RANDOM.nextInt(COURSE_NAMES.length)];

        int count = (int) getEnrollmentCountsMapStream(
                enrollments.stream()
                        .filter(enrollment -> enrollment.getCourseName().contains(randomCourseName))
                        .toList())
                .count();

        // test
        List<Student> students = studentOperationsRepository
                .findStudentsEnrolledInCourse(randomCourseName, Pageable.unpaged());

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudents_hasAnyGradeEqualsGiven_ShouldPass() {
        // create
        Grade randomGrade = Grade.C;

        int count = (int) getEnrollmentCountsMapStream(
                enrollments.stream()
                        .filter(enrollment -> randomGrade.equals(enrollment.getGrade()))
                        .toList())
                .count();

        // test
        List<Student> students = studentOperationsRepository
                .findStudentsWithGradeEqualTo(randomGrade, Pageable.unpaged());

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudents_bySpecificGradeInGivenCourse_ShouldPass() {
        // create
        String randomCourseName = COURSE_NAMES[RANDOM.nextInt(COURSE_NAMES.length)];
        Grade randomGrade = Grade.B;

        int count = (int) getEnrollmentCountsMapStream(
                enrollments.stream()
                        .filter(enrollment -> randomGrade.equals(enrollment.getGrade()) &&
                                enrollment.getCourseName().contains(randomCourseName))
                        .toList())
                .count();

        // test
        List<Student> students = studentOperationsRepository
                .findStudentsWithGradeEqualToInCourse(randomGrade, randomCourseName, Pageable.unpaged());

        // assert
        assertEquals(count, students.size());
    }

    private Stream<Map.Entry<String, Long>> getEnrollmentCountsMapStream(List<Enrollment> enrollments) {
        return enrollments.stream()
                .collect(groupingBy(enrollment -> enrollment.getRelation().getParent(), counting()))
                .entrySet()
                .stream();
    }

}
