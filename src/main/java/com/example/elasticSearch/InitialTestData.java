package com.example.elasticSearch;

import com.example.elasticSearch.models.Course;
import com.example.elasticSearch.models.Enrollment;
import com.example.elasticSearch.models.Grade;
import com.example.elasticSearch.models.Student;
import com.example.elasticSearch.repositories.CourseRepository;
import com.example.elasticSearch.repositories.EnrollmentRepository;
import com.example.elasticSearch.repositories.StudentRepository;
import com.example.elasticSearch.repositories.custom.IndexOperationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.join.JoinField;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

import static com.example.elasticSearch.models.Enrollment.ENROLLMENT_RELATION_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialTestData implements CommandLineRunner {

    public static final Random RANDOM = new Random();

    public static final String[] STUDENT_NAMES =
            {"John Doe", "John Doe Smith", "Bob Doe",  "john Dody Doe", "Bob Dody Doe", "Jane Smith", "Alice Brown",
                    "Bob White", "Charlie Black"};
    public static final String[] EMAIL_VENDORS =
            {"@gmail.com", "@yahoo.com", "@outlook.com", "@icloud.com", "@aol.com", "@zoho.com", "@protonmail.com"};

    public static final String[] COURSE_NAMES =
            {"Intro to Chemistry", "Advanced Physics", "Fundamentals of Computer Science", "Principles of Economics",
                    "Topics in Philosophy", "Advanced Maths", "Fundamentals of Economics", "Intro to Databases"};
    public static final int[] CREDITS = {1, 2, 3, 4, 5, 6, 7, 8};


    public static List<Student> students;
    public static List<Course> courses;
    public static List<Enrollment> enrollments;


    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final IndexOperationsRepository indexOperationsRepository;

    @Value("${elasticsearch.schema.update}")
    private boolean updateSchema;

    @Value("${elasticsearch.indices.deletable}")
    private String[] deletableIndices;

    @Override
    public void run(String... args) throws Exception {

        if(updateSchema && Objects.nonNull(deletableIndices)) {
            Arrays.stream(deletableIndices).forEach(indexOperationsRepository::deleteIndex);
            indexOperationsRepository.createIndex(Student.class);
            indexOperationsRepository.createIndex(Course.class);
        }

        if(courseRepository.count() == 0) {
            courses = initCourses();
            courseRepository.saveAll(courses);
        } else {
            courses = courseRepository.findAll();
        }
        if(studentRepository.count() == 0) {
            students = initStudents();
            studentRepository.saveAll(students);

            enrollments = initEnrollments();
            enrollmentRepository.saveAll(enrollments);

        } else {
            students = studentRepository.findAllStudents();
            enrollments = enrollmentRepository.findAll();
        }

        log.info("student repository count = {}", studentRepository.count());
        log.info("course repository count = {}", courseRepository.count());
        log.info("enrollment repository count = {}", enrollmentRepository.count());
    }

    private static List<Student> initStudents() {
        List<Student> students = new ArrayList<>();
        IntStream.range(1, 100).forEach(index -> {
            String name =
                    STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)]+ " " + index;
            String email =
                    name.replace(" ", "").toLowerCase() +
                            EMAIL_VENDORS[RANDOM.nextInt(EMAIL_VENDORS.length)];
            int age = RANDOM.nextInt(32) + 18; // from 18 to 50

            students.add(new Student(name, age, email));
        });
        return students;
    }

    private static List<Course> initCourses() {
        List<Course> courses = new ArrayList<>();
        IntStream.range(0, COURSE_NAMES.length).forEach(index -> {
            Course course = new Course();
            course.setName(COURSE_NAMES[index]);
            course.setCredits(CREDITS[Math.min(index, CREDITS.length - 1)]);
            course.setDescription("some long description for course " + COURSE_NAMES[index]);
            courses.add(course);
        });

        return courses;
    }

    private static List<Enrollment> initEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        Grade[] grades = Grade.values();

        IntStream.range(0, students.size()).forEach(index -> {
            Student student = students.get(index);
            Set<Course> chosenCourses = new HashSet<>();
            IntStream.range(0, courses.size()).forEach(innerIndex -> {
                chosenCourses.add(courses.get(RANDOM.nextInt(courses.size())));
            });
            chosenCourses.forEach(course -> {
                enrollments.add(new Enrollment(
                        null,
                        grades[RANDOM.nextInt(grades.length)],
                        "some notes",
                        course.getId(),
                        course.getName(),
                        new JoinField<>(ENROLLMENT_RELATION_NAME, student.getId())));
            });
        });
        return enrollments;
    }

}
