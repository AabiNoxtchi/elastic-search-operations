package com.example.elasticSearch.repositories;


import com.example.elasticSearch.models.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.elasticSearch.InitialTestData.*;
import static com.example.elasticSearch.InitialTestData.students;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @Test
    public void filterStudentsByName_providingPartNameDifferentCase_ShouldPass() {
        // create
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase();
        int count = (int) students.stream()
                .filter(student -> student.getName().toLowerCase().contains(nameToFilter))
                .count();

        // test
        // find in elasticsearch repository by default finds text type strings:
        // containing the given string, case-insensitive search
        List<Student> students = studentRepository.findByName(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameContainingDefault_providingPartNameDifferentCase_ShouldPass() {
        // create
        // default (containing or startWith) does not work with space in String with text type fields
        // need to explicitly define behaviour with @query annotation
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].split(" ")[0].toLowerCase();
        int count = (int) students.stream().filter(student -> student.getName().toLowerCase().contains(nameToFilter))
                .count();

        // test
        List<Student> students = studentRepository.findByNameContaining(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameContainingMatchQuery_providingPartNameDifferentCase_ShouldPass() {
        // create
        // a match query will tokenize the query string and search by all/any of the tokens exist for text type fields
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase();
        String[] namesToFilter = nameToFilter.split(" ");
        int count = (int) students.stream()
                .filter(student ->
                        Arrays.stream(namesToFilter).anyMatch(name -> student.getName().toLowerCase().contains(name)))
                .count();

        // test
        List<Student> students = studentRepository.findByNameContainingByMatchQuery(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameContainingAllTokenizedGivenString_providingPartNameDifferentCase_ShouldPass() {
        // create
        // a match query will tokenize the query string and search by all the tokens exist for text type fields
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase();
        String[] namesToFilter = nameToFilter.split(" ");
        int count = (int) students.stream()
                .filter(student ->
                        Arrays.stream(namesToFilter).allMatch(name -> student.getName().toLowerCase().contains(name)))
                .count();

        // test
        List<Student> students = studentRepository.findByNameContainingAllTokenizedSearchName(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameContainingWildCardQuery_providingPartNameDifferentCase_ShouldPass() {
        // create
        // default (containing or startWith) does not work with space in String with text type fields
        // one option to search the given string as-is: using field.keyword in @query
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase();
        int count = (int) students.stream()
                .filter(student -> student.getName().toLowerCase().contains(nameToFilter))
                .count();

        // test
        List<Student> students = studentRepository.findByNameContainingWildCardQuery(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameContainingMatchPhraseQuery_providingPartNameDifferentCase_ShouldPass() {
        // create
        // default (containing or startWith) does not work with space in String with text type fields
        // one option to search the given string as-is:
        // using match_phrase with slop: 0 (no other strings between the given string tokens)
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase();

        int count = (int) students.stream()
                .filter(student -> student.getName().toLowerCase().contains(nameToFilter))
                .count();

        // test
        List<Student> students = studentRepository.findByNameContainingByMatchPhrase(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameExactMatch_providingPartNameIgnoreCase_ShouldReturnEmptyStudents() {
        // create
        String nameToFilter = STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase();
        int count = (int) students.stream().filter(student -> student.getName().equals(nameToFilter)).count();

        // test
        List<Student> students = studentRepository.findExactMatchByName(nameToFilter);

        // assert
        assertEquals(0, count);
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameExactMatch_providingFullName_ShouldPass() {
        // create
        String nameToFilter = students.get(RANDOM.nextInt(students.size())).getName();
        int count = (int) students.stream().filter(student -> student.getName().equals(nameToFilter)).count();

        // test
        List<Student> students = studentRepository.findExactMatchByName(nameToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByNameIn_providingPartNamesDifferentCase_ShouldPass() {
        // create
        List<String> namesToFilter = new ArrayList<>();
        IntStream.range(0, 5).forEach(number -> {
            namesToFilter.add(STUDENT_NAMES[RANDOM.nextInt(STUDENT_NAMES.length)].toLowerCase());
        });
        int count = (int) students.stream()
                .filter(student ->
                        namesToFilter.stream().anyMatch(name -> student.getName().toLowerCase().contains(name)))
                .count();

        // test
        List<Student> students = studentRepository.findByNameIn(namesToFilter);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByAgeBetween_providingMinAndMaxAge_ShouldPass() {
        // create
        int minAge = 20;
        int maxAge = 30;
        int count = (int) students.stream()
                .filter(student -> student.getAge() >= minAge && student.getAge() <= maxAge)
                .count();

        // test
        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void filterStudentsByAgeGreaterAndLessThan_providingMinAndMaxAge_ShouldPass() {
        // create
        int minAge = 20;
        int maxAge = 30;
        int count = (int) students.stream()
                .filter(student -> student.getAge() > minAge && student.getAge() < maxAge)
                .count();

        // test
        List<Student> students = studentRepository.findByAgeGreaterThanAndAgeLessThan(minAge, maxAge);

        // assert
        assertEquals(count, students.size());
    }

    @Test
    public void SortStudentsByAge_ShouldPass() {
        // create
        int minAge = students.stream().mapToInt(Student::getAge).min().orElse(0);
        int maxAge = students.stream().mapToInt(Student::getAge).max().orElse(50);
        int count = students.size();

        // test
        List<Student> students = (List<Student>) studentRepository.findAll(Sort.by("age").ascending());

        // assert
        assertFalse(students.isEmpty());
        assertEquals(count, students.size());
        assertEquals(minAge, students.get(0).getAge());
        assertEquals(maxAge, students.get(students.size() - 1).getAge());
    }

}
