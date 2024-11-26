package com.example.elasticSearch.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "course")
public class Course {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    private int credits;

    @Field(type = FieldType.Text)
    private String description;
}

