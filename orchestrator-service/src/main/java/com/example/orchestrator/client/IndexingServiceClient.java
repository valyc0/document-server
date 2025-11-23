package com.example.orchestrator.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class IndexingServiceClient {

    private final RestClient restClient;
    private final String indexingServiceUrl;

    public IndexingServiceClient(
            RestClient restClient,
            @Value("${indexing.service.url:http://indexing-service:8082}") String indexingServiceUrl) {
        this.restClient = restClient;
        this.indexingServiceUrl = indexingServiceUrl;
    }

    public List<Map<String, Object>> search(String query, Integer maxResults) {
        String url = indexingServiceUrl + "/api/search?q=" + query + "&maxResults=" + maxResults;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    public List<Map<String, Object>> searchPost(Map<String, Object> request) {
        String url = indexingServiceUrl + "/api/search/query";
        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    public List<Map<String, Object>> searchRaw(String query) {
        String url = indexingServiceUrl + "/api/search/raw?q=" + query;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    public List<String> getIndexedFiles() {
        String url = indexingServiceUrl + "/api/search/files";
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public void deleteDocument(String documentId) {
        String url = indexingServiceUrl + "/api/search/documents/" + documentId;
        restClient.delete()
                .uri(url)
                .retrieve()
                .toBodilessEntity();
    }
}
