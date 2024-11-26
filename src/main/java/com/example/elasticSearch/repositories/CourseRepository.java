package com.example.elasticSearch.repositories;

import com.example.elasticSearch.models.Course;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String> {
    List<Course> findAll();
}
