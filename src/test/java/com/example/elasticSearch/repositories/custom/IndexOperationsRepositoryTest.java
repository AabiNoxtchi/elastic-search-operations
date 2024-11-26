package com.example.elasticSearch.repositories.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static com.example.elasticSearch.models.Student.STUDENT_INDEX_NAME;

@SpringBootTest
public class IndexOperationsRepositoryTest {

    @Autowired
    IndexOperationsRepository indexOperationsRepository;

    ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    public void getMappings_studentClass() {

        // test
        Map<String, Object> studentMapping = indexOperationsRepository.getMapping(STUDENT_INDEX_NAME);

        try {
            String jsonString = objectMapper.writeValueAsString(studentMapping);
            System.out.println(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @Disabled("Don't delete indices every time we run tests")
    public void deleteIndices() {
        indexOperationsRepository.deleteIndex("enrollment");
        indexOperationsRepository.deleteIndex("student");
        indexOperationsRepository.deleteIndex("course");
        indexOperationsRepository.deleteIndex("student_v2");
    }
}
