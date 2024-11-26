package com.example.elasticSearch.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.join.JoinField;

import static com.example.elasticSearch.models.Enrollment.ENROLLMENT_RELATION_NAME;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = Student.STUDENT_INDEX_NAME)
public class Student {

    public static final String STUDENT_INDEX_NAME = "student_v2";

    @Id
    private String id;

    private String name;
    private int age;
    private String email;

    @JoinTypeRelations(relations = {@JoinTypeRelation(parent = "student", children = {ENROLLMENT_RELATION_NAME})})
    private JoinField<String> relation = new JoinField<>("student");

    public Student(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
}

