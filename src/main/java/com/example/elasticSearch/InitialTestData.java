package com.example.elasticSearch;

import com.example.elasticSearch.models.document.Student;
import com.example.elasticSearch.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialTestData implements CommandLineRunner {

    public static final Random RANDOM = new Random();

    public static final String[] STUDENT_NAMES =
            {"John Doe", "John Doe Smith", "Bob Doe",  "john Dody Doe", "Bob Dody Doe", "Jane Smith", "Alice Brown", "Bob White", "Charlie Black"};
    public static final String[] EMAIL_VENDORS =
            {"@gmail.com", "@yahoo.com", "@outlook.com", "@icloud.com", "@aol.com", "@zoho.com", "@protonmail.com"};

    public static List<Student> students;

    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {

        if(studentRepository.count() == 0) {
            students = initStudents();
            studentRepository.saveAll(students);
        } else {
            students = studentRepository.findAllStudents();
        }

        log.info("student repository count = {}", studentRepository.count());
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

            students.add(new Student(null, name, age, email));
        });
        return students;
    }
}
