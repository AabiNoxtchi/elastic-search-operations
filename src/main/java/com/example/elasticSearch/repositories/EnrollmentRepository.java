package com.example.elasticSearch.repositories;

import com.example.elasticSearch.models.Enrollment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends ElasticsearchRepository<Enrollment, String> {
    List<Enrollment> findAll();
}
