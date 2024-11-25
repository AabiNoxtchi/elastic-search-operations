package com.example.elasticSearch.models.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "student")
public class Student {
    @Id
    private String id;

    private String name;
    private int age;
    private String email;

}

