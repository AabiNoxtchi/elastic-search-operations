package com.example.elasticSearch.repositories;

import com.example.elasticSearch.models.Student;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StudentRepository extends ElasticsearchRepository<Student, String> {

    @Query("{ \"match_all\": {} }")
    List<Student> findAllStudents();

    List<Student> findByName(String name);

    List<Student> findByNameContaining(String name);

    @Query("{\"match\": {\"name\": \"?0\"}}")
    List<Student> findByNameContainingByMatchQuery(String name);

    @Query("""
    {
        "bool": {
            "must": [
                {
                    "match": {
                        "name": {
                            "query": "?0",
                            "operator": "AND",
                            "minimum_should_match": "100%"
                        }
                    }
                }
            ]
        }
    }
    """)
    List<Student> findByNameContainingAllTokenizedSearchName(String nameToFilter);

    @Query("""
    {
        "wildcard": {
            "name.keyword": {
                "value": "*?0*",
                "case_insensitive": true
            }
        }
    }
    """)
    List<Student> findByNameContainingWildCardQuery(String name);

    @Query("""
    {
        "bool": {
            "must": [
                {
                    "match_phrase": {
                        "name": {
                            "query": "?0",
                            "slop": 0
                        }
                    }
                }
            ]
        }
    }
""")
    List<Student> findByNameContainingByMatchPhrase(String name);

    @Query("{\"term\": {\"name.keyword\": \"?0\"}}")
    List<Student> findExactMatchByName(String name);

    List<Student> findByNameIn(List<String> names);

    List<Student> findByAgeBetween(int minAge, int maxAge);

    List<Student> findByAgeGreaterThanAndAgeLessThan(int minAge, int maxAge);
}

