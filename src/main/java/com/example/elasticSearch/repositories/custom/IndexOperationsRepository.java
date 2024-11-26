package com.example.elasticSearch.repositories.custom;

import java.util.Map;

public interface IndexOperationsRepository {

    boolean deleteIndex(String indexName);

    void createIndex(Class classType);

    Map<String, Object> getMapping(String indexName);
}
