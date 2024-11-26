package com.example.elasticSearch.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.join.JoinField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = Student.STUDENT_INDEX_NAME)
public class Enrollment {

    public final static String ENROLLMENT_RELATION_NAME = "enrollment";

    @Id
    private String id;

    private Grade grade;
    private String notes;

    @NotBlank
    @Field(type = FieldType.Keyword)
    private String courseId;

    @NotBlank
    @Field(type = FieldType.Keyword)
    private String courseName; // Denormalized course name

    @NotNull
    // todo: custom annotation ensuring parent is not null
    @JoinTypeRelations(
            relations = {
                    @JoinTypeRelation(parent = "student", children = {ENROLLMENT_RELATION_NAME})
            }
    )
    private JoinField<String> relation;
}

