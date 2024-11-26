package com.example.elasticSearch.repositories.custom;


import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.elasticSearch.models.Grade;
import com.example.elasticSearch.models.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;


import java.util.List;

import static com.example.elasticSearch.models.Enrollment.ENROLLMENT_RELATION_NAME;

@Repository
@RequiredArgsConstructor
public class StudentOperationsRepositoryImpl implements StudentOperationsRepository{

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<Student> findStudentsWithEnrollmentsMoreThan(int enrollmentsCount, Pageable pageable) {

        // Build the query using the Elasticsearch Java API
        Query query = Query.of(qb -> qb // qb query.Builder
                .hasChild( hc -> hc
                        .type(ENROLLMENT_RELATION_NAME) // Specify the child type
                        //.queryName("enrollmentQuery") // Optional query name
                        .query(Query.of(q -> q
                                .matchAll(m -> m))) // Match all child docs
                        .minChildren(enrollmentsCount + 1) // Minimum children for a parent
                        .scoreMode(ChildScoreMode.None)
                )
        );

        return executeNativeQuery(query, pageable);
    }

    @Override
    public List<Student> findStudentsWithEnrollmentsCountBetween(int minCount, int maxCount, Pageable pageable) {
        // Build the query using the Elasticsearch Java API
        Query query = Query.of(qb -> qb
                .hasChild(
                        hc -> hc
                        .type(ENROLLMENT_RELATION_NAME) // Specify the child type
                        //.queryName("enrollmentQuery") // Optional query name
                        .query(Query.of(q -> q
                                .matchAll(m -> m))) // Match all child docs
                        .minChildren(minCount + 1) // Minimum children for a parent
                        .maxChildren(maxCount - 1)
                        .scoreMode(ChildScoreMode.None)
                )
        );

        return executeNativeQuery(query, pageable);
    }

    @Override
    public List<Student> findStudentsEnrolledInCourse(String courseName, Pageable pageable) {
        // Build the query to filter students by course name
        Query query = Query.of(qb -> qb
                .hasChild(hc -> hc
                        .type(ENROLLMENT_RELATION_NAME) // Specify the child type
                        .query(Query.of(q -> q
                                .match(m -> m
                                        .field("courseName.keyword") // Match the courseName field
                                        .query(courseName)))) // Match the provided course name
                        .minChildren(1)
                        .scoreMode(ChildScoreMode.None) // No scoring aggregation
                )
        );

        return executeNativeQuery(query, pageable);
    }

    @Override
    public List<Student> findStudentsWithGradeEqualTo(Grade grade, Pageable pageable) {
        // Build the query to filter students by grade
        Query query = Query.of(qb -> qb
                .hasChild(hc -> hc
                        .type(ENROLLMENT_RELATION_NAME) // Specify the child type
                        .query(Query.of(q -> q
                                .match(m -> m
                                        .field("grade.keyword") // Match the grade field
                                        .query(grade.name())))) // Match the provided grade
                        .minChildren(1)
                        .scoreMode(ChildScoreMode.None) // No scoring aggregation
                )
        );

        return executeNativeQuery(query, pageable);
    }

    @Override
    public List<Student> findStudentsWithGradeEqualToInCourse(Grade grade, String courseName, Pageable pageable) {
        Query query = Query.of(qb -> qb
                .hasChild(hc -> hc
                        .type(ENROLLMENT_RELATION_NAME) // Specify the child type
                        .query(Query.of(q -> q
                                .bool(b -> b
                                        .must(m -> m.match(mt -> mt
                                                .field("courseName.keyword") // Match courseName field
                                                .query(courseName)  // Match the provided course name
                                        ))
                                        .must(m -> m.match(mt -> mt
                                                .field("grade.keyword") // Match grade field
                                                .query(grade.name()) // Match the specific grade
                                        ))
                                )
                        ))
                        .scoreMode(ChildScoreMode.None) // No scoring aggregation
                )
        );

        return executeNativeQuery(query, pageable);
    }

    private List<Student> executeNativeQuery(Query query, Pageable pageable) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query) // Attach the query
                .withPageable(pageable)
                .build();

        // Execute the query and map results to Student objects
        return elasticsearchOperations.search(nativeQuery, Student.class)
                .stream()
                .map(SearchHit::getContent) // Extract Student objects
                .toList();
    }
}
