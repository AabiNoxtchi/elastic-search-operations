# Getting Started  


**Elasticsearch** is a distributed, open-source search and analytics engine built on Apache Lucene.
It is widely used for full-text search, logging, and real-time data analysis.  

**Elasticsearch** is often referred to as a search engine rather than a traditional database because its core functionality
revolves around search and analytics rather than general-purpose data storage and management.
However, it can store data like a database, but its primary strength lies in indexing and searching that data efficiently:

- Search Engine: Unlike traditional databases, Elasticsearch is optimized for text search, enabling fast, full-text queries and complex data retrieval.

- Database-like: It stores data in a structured way using indices (similar to tables in a database) and documents (similar to rows),
  but it is not built for transactional operations or relational data.

- Not a General-Purpose Database: While it can store and retrieve data, it lacks many features of traditional databases
  like complex joins, ACID compliance, and the full breadth of relational data modeling.

So, in summary, Elasticsearch is a specialized engine designed for fast, scalable search and analytics, not a full-fledged database system.

**Key Features:**
- Search: High-speed full-text search with support for complex queries.
- Scalability: Easily scales horizontally to handle large datasets (increasing nodes in a cluster).
- Real-time Analytics: Enables near real-time data querying and visualizations.
- Integration: Works seamlessly with tools like Kibana (visualization) and Logstash (data ingestion).  

**Why Use Elasticsearch?**
- Performance: Optimized for rapid searches and analytics over massive datasets.
- Flexibility: Supports structured, unstructured, and geospatial data.
- Ease of Use: Simple RESTful APIs and JSON-based queries.
- Community: Strong ecosystem with active community support.
- Ideal for applications like log monitoring, e-commerce product searches, and system metrics tracking.  

**usage in repositories and test packages**
****  
### Using ElasticsearchRepository 

Elasticsearch repositories comes with predefined methods and allow custom query definitions.  
1. **predefined methods**: `ElasticsearchRepository` inherits methods from the following interfaces:
- CrudRepository
- PagingAndSortingRepository
- ElasticsearchRepository itself adds additional Elasticsearch-specific features:  
  - `Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable)`: 
Finds documents similar to a given entity, based on specified fields and pagination.
  
  - `<S extends T> S save(S entity, @Nullable RefreshPolicy refreshPolicy)`: 
Saves a single entity to the Elasticsearch index with an optional refresh policy.

  - `<S extends T> Iterable<S> saveAll(Iterable<S> entities, @Nullable RefreshPolicy refreshPolicy)`: 
Saves multiple entities to the Elasticsearch index with an optional refresh policy.

  - `void deleteById(ID id, @Nullable RefreshPolicy refreshPolicy)`: 
Deletes a document by its ID, applying an optional refresh policy.

  - `void delete(T entity, @Nullable RefreshPolicy refreshPolicy)`: 
Deletes a specific document, applying an optional refresh policy.

  - `void deleteAllById(Iterable<? extends ID> ids, @Nullable RefreshPolicy refreshPolicy)`: 
Deletes multiple documents by their IDs, with an optional refresh policy.

  - `void deleteAll(Iterable<? extends T> entities, @Nullable RefreshPolicy refreshPolicy)`: 
Deletes multiple documents from the Elasticsearch index, applying an optional refresh policy.

  - `void deleteAll(@Nullable RefreshPolicy refreshPolicy)`: 
Deletes all documents of the given type from the Elasticsearch index, with an optional refresh policy.
<br>  
  
2. **Custom Query Methods:**  
- By deriving the query from the method name directly:
  - `findBy<FieldName>(...)`
  - `findBy<FieldName>Between(...)`
  - `findBy<FieldName>Containing(...)`
  - The naming conventions like `findAllStudentsOrderByField()` that work in Spring Data JPA are not directly supported in Spring Data Elasticsearch.
    Elasticsearch requires explicit sorting in the query DSL, which cannot be derived automatically from the method name.
    Query Translation: Spring Data Elasticsearch does not translate method names with OrderBy into Elasticsearch's sort field internally.
    **solution**: Using one of `PagingAndSortingRepository` methods that `ElasticsearchRepository` inherits
    ```java
    Iterable<T> findAll(Sort sort);
    
    Page<T> findAll(Pageable pageable);
    ```    
   <br>
- By using a [manually defined query using @Query for advanced queries](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/repositories/elasticsearch-repository-queries.html)
<br><br>  
*******  
### Text fields search
1. Designed for Full-Text Search
   Elasticsearch is built on Apache Lucene, which is optimized for searching text. It indexes data in a way that supports 
analyzing text into individual tokens (e.g., words or phrases). When a query is executed, Elasticsearch tries to find documents 
containing terms or tokens that match those in the query, even if they're not exact matches.
2. Tokenization
   When a document is indexed in Elasticsearch, text fields are analyzed and split into individual terms (tokens)
by the analyzer. For instance, the string "ElasticSearch is great" might be tokenized into ["elasticsearch", "is", "great"].
During a search, Elasticsearch compares these tokens with the query's tokens. This "contains" logic is a natural result of tokenization and how inverted indices work.
3. Relevance Scoring
   Elasticsearch ranks results based on their relevance, using scoring mechanisms like TF-IDF (Term Frequency-Inverse Document Frequency)
or BM25. A "contains" approach ensures that documents are ranked according to how well their content matches the query terms,
which is more useful for search scenarios than strict exact matching.
4. Flexibility
   The "contains" approach offers flexibility in querying: It handles partial matches better than an exact match would.
For example, a search for "elastic" would still match "elasticsearch", making it user-friendly.
5. Customizable Behavior
   If exact matching is required, Elasticsearch allows this through its rich query capabilities. For instance:
- Using a [keyword](https://www.elastic.co/guide/en/elasticsearch/reference/current/keyword.html) field for exact matching without analysis.
- Use queries like term, match_phrase, or wildcard for stricter or customized search behavior:
   ```java
    @Query("{\"term\": {\"name.keyword\": \"?0\"}}")
    List<MyEntity> findExactMatchByName(String name);
    ```  
    - term Query: A term query is used for exact matches. It does not analyze the input (e.g., no tokenization or lowercase conversion).
It ensures that the value in the query matches the stored value in Elasticsearch exactly.
    - name.keyword: Refers to the keyword subfield of the name field in Elasticsearch. The keyword type stores the exact value 
  of a text field (case-sensitive), unlike text fields that are analyzed and tokenized for full-text search. If the name field
  is defined as text with a keyword subfield, the query is specifically targeting the exact value stored in the keyword subfield.

****
### Elasticsearch Command Line (cURL) Commands

Useful commands to check and debug elasticsearch through cmd:  
1. Check All Indices
   To view all indices in the Elasticsearch cluster:
   ```shell
    curl -X GET "localhost:9200/_cat/indices?v"
    ```
   
2. View Index Mapping
   To check the mapping of a specific index:
   ```shell
    curl -X GET "localhost:9200/<index_name>/_mapping?pretty"
    ```
   
3. View Index Data
   To retrieve documents from an index using a query:
   ```shell
    curl -X GET "localhost:9200/student_v2/_search?pretty" -H "Content-Type: application/json" -d "{\"query\":{\"has_child\":{\"type\":\"enrollment\",\"query\":{\"match_all\":{}},\"min_children\":6}}}" 
    curl -X GET "localhost:9200/student_v2/_search?pretty" -H "Content-Type: application/json" -d "{\"query\":{\"match_all\":{}}}"
    ```
*************
### Spring Boot elasticsearch clients

Spring Boot provides seamless integration with Elasticsearch through various clients, it offers both imperative and reactive Elasticsearch clients.  
**The imperative client** is synchronous and blocks the thread for each operation, making it suitable for traditional applications.  
**The reactive client** is non-blocking and leverages Project Reactor, enabling high-performance, asynchronous interactions ideal for reactive programming models.  

**List of Spring Boot Elasticsearch Clients:**  
1. Elasticsearch Rest High-Level Client (Deprecated):
A synchronous client that was the default for older Spring Boot versions using Elasticsearch <8.x.
Deprecated as of Elasticsearch 7.15 and replaced by the Elasticsearch Java Client in Elasticsearch 8.x.
2. Elasticsearch Java Client (Recommended):
The official modern client introduced in Elasticsearch 8.x.
Fully supports synchronous and asynchronous programming.
Recommended by Elastic as the replacement for the High-Level Client.
3. Elasticsearch Reactive Client:
A non-blocking, reactive client based on Spring WebFlux and Project Reactor.
Ideal for applications requiring asynchronous operations and reactive programming models.
4. Spring Data Elasticsearch:
A higher-level abstraction provided by Spring, integrating Elasticsearch with Spring Boot.
Built on top of the Elasticsearch Java Client and supports repository and template-based data access.
**Default Client in Spring Boot:**
The default client depends on the Spring Data Elasticsearch version and Elasticsearch version:
Spring Boot <2.6: Used the Rest High-Level Client as the default.
Spring Boot 2.6+: Transitioned to using the Elasticsearch Java Client by default, aligning with Elasticsearch 8.x.

These clients provide the following beans to work with:
1. RestClient:
   - Level: Low-level HTTP.
   - Purpose: Handles raw HTTP requests to Elasticsearch. Part of the Rest High-Level Client.
   - Use Case: Low-level direct communication with Elasticsearch.
2. ElasticsearchClient:
    - Level: High-level API.
    - Purpose: Provides a modern, feature-rich Java API for interacting with Elasticsearch, built on top of RestClient.
    - Use Case: General-purpose client for Elasticsearch 8.x+ (imperative and reactive APIs).
3. ElasticsearchOperations:
    - Level: Spring abstraction.
    - Purpose: A higher-level abstraction provided by Spring Data Elasticsearch for working with Elasticsearch in Spring applications.
    - Use Case: Spring-based applications performing common Elasticsearch operations (CRUD, queries).
*******

### Spring Boot Elasticsearch Field, Objects and Relational Types

1. [Elasticsearch data types](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html#object-types)
2. [Object mappings](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/object-mapping.html)
  

<br><br><br>
### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.5/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.5/gradle-plugin/packaging-oci-image.html)
* [Spring Data Elasticsearch (Access+Driver)](https://docs.spring.io/spring-boot/3.3.5/reference/data/nosql.html#data.nosql.elasticsearch)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

