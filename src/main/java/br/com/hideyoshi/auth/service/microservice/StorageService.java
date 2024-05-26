package br.com.hideyoshi.auth.service.microservice;

import br.com.hideyoshi.auth.config.StorageServiceConfig;
import br.com.hideyoshi.auth.enums.FileTypeEnum;
import br.com.hideyoshi.auth.model.microservice.StorageServiceDownloadResponse;
import br.com.hideyoshi.auth.model.microservice.StorageServiceUploadResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class StorageService {

    private final ObjectMapper objectMapper;

    private final StorageServiceConfig storageServiceConfig;

    private final String PARAMETER_FILE_KEY = "file_key";

    private final String PARAMETER_FILE_POSTFIX = "file_postfix";

    private final String PARAMETER_FILE_TYPE = "file_type";

    public Optional<StorageServiceUploadResponse> getNewFileUrl(String username, String filePostfix, FileTypeEnum fileTypeEnum) {
        HashMap<String, String> values = new HashMap<>() {{
            put(PARAMETER_FILE_KEY, username);
            put(PARAMETER_FILE_POSTFIX, filePostfix);
            put(PARAMETER_FILE_TYPE, fileTypeEnum.getFileExtension());
        }};

        URI uri = URI.create(storageServiceConfig.getFileServicePath() + "/file");
        String requestBody = this.writeToRequestBody(values);

        StorageServiceUploadResponse uploadResponse = null;
        try {
            var response = this.postRequest(uri, requestBody);
            uploadResponse = objectMapper.readValue(response, StorageServiceUploadResponse.class);
        } catch (IOException e) {
            log.warn("File not found: " + username + "/" + filePostfix);
        }

        return Optional.ofNullable(uploadResponse);
    }

    public Optional<StorageServiceDownloadResponse> getFileUrl(String username, String filePostfix) {
        URI uri;
        try {
            uri = new URIBuilder(storageServiceConfig.getFileServicePath() + "/file")
                    .addParameter(PARAMETER_FILE_KEY, username)
                    .addParameter(PARAMETER_FILE_POSTFIX, filePostfix)
                    .build();
        } catch (URISyntaxException e) {
            log.warn("Invalid File: " + username + "/" + filePostfix);
            return Optional.empty();
        }


        StorageServiceDownloadResponse downloadResponse = null;
        try {
            var responseString = this.getRequest(uri);
            downloadResponse = objectMapper.readValue(responseString, StorageServiceDownloadResponse.class);
        } catch (IOException e) {
            log.warn("File not found: " + username + "/" + filePostfix);
        }

        return Optional.ofNullable(downloadResponse);
    }

    public void deleteFile(String username, String filePostfix) {
        URI uri = null;
        try {
            uri = new URIBuilder(storageServiceConfig.getFileServicePath() + "/file")
                    .addParameter(PARAMETER_FILE_KEY, username)
                    .addParameter(PARAMETER_FILE_POSTFIX, filePostfix)
                    .build();
        } catch (URISyntaxException e) {
            log.warn("File not found: " + username + "/" + filePostfix);
        }

        try {
            this.deleteRequest(uri);
        } catch (IOException e) {
            log.warn("File not found: " + username + "/" + filePostfix);
        }
    }

    public void processFile(String username, String filePostfix) {
        HashMap<String, String> values = new HashMap<>() {{
            put(PARAMETER_FILE_KEY, username);
            put(PARAMETER_FILE_POSTFIX, filePostfix);
        }};

        URI uri = URI.create(storageServiceConfig.getFileServicePath() + "/file/process");
        String requestBody = this.writeToRequestBody(values);

        try {
            this.postRequest(uri, requestBody);
        } catch (IOException e) {
            log.warn("File not found: " + username + "/" + filePostfix);
        }
    }

    protected String getRequest(URI requestURI) throws IOException {
        HttpGet request = new HttpGet(requestURI);
        request.setHeader("Content-Type", "application/json");

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        return httpClient.execute(
                request,
                response -> EntityUtils.toString(response.getEntity(), "UTF-8")
        );
    }

    protected String postRequest(URI requestURI, String requestBody) throws IOException {
        HttpPost request = new HttpPost(requestURI);
        request.setHeader("Content-Type", "application/json");

        request.setEntity(new ByteArrayEntity(requestBody.getBytes(StandardCharsets.UTF_8)));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        return httpClient.execute(
                request,
                response -> EntityUtils.toString(response.getEntity(), "UTF-8")
        );
    }

    protected void deleteRequest(URI requestURI) throws IOException {
        HttpDelete request = new HttpDelete(requestURI);
        request.setHeader("Content-Type", "application/json");

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        httpClient.execute(
                request,
                response -> {
                    return null;
                }
        );
    }

    private String writeToRequestBody(HashMap<String, String> values) {
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

}
