package com.example.elasticSearch.repositories.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class IndexOperationsRepositoryImpl implements IndexOperationsRepository{

    private final ElasticsearchOperations elasticsearchOperations;

    public boolean deleteIndex(String indexName) {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
        return indexOperations.delete();
    }

    @Override
    public void createIndex(Class classType) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(classType);

        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
        }
    }

    @Override
    public Map<String, Object> getMapping(String indexName) {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
        return indexOperations.getMapping();
    }
}
