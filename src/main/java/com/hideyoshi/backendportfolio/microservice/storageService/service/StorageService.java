package com.hideyoshi.backendportfolio.microservice.storageService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hideyoshi.backendportfolio.microservice.storageService.config.StorageServiceConfig;
import com.hideyoshi.backendportfolio.microservice.storageService.enums.FileTypeEnum;
import com.hideyoshi.backendportfolio.microservice.storageService.model.StorageServiceDownloadResponse;
import com.hideyoshi.backendportfolio.microservice.storageService.model.StorageServiceUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

@Log4j2
@Service
@RequiredArgsConstructor
public class StorageService {

    private final ObjectMapper objectMapper;

    private final StorageServiceConfig storageServiceConfig;

    private final String PARAMETER_USERNAME = "username";

    private final String PARAMETER_FILE_POSTFIX = "file_postfix";

    private final String PARAMETER_FILE_TYPE = "file_type";

    private final String PARAMETER_KEY_STRING = "string_url";

    public StorageServiceUploadResponse getNewFileUrl(String username, String filePostfix, FileTypeEnum fileTypeEnum) {
        HashMap<String, String> values = new HashMap<>() {{
            put(PARAMETER_USERNAME, username);
            put(PARAMETER_FILE_POSTFIX, filePostfix);
            put(PARAMETER_FILE_TYPE, fileTypeEnum.getFileExtension());
        }};

        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpPost request = new HttpPost(URI.create(storageServiceConfig.getFileServicePath() + "/file"));
        request.setHeader("Content-Type", "application/json");

        try {
            request.setEntity(new ByteArrayEntity(requestBody.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        try {
            return httpClient.execute(
                    request,
                    response -> {
                        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                        return objectMapper.readValue(responseString, StorageServiceUploadResponse.class);
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StorageServiceDownloadResponse getFileUrl(String username, String filePostfix) {
        URI uri = null;
        try {
            uri = new URIBuilder(storageServiceConfig.getFileServicePath() + "/file")
                    .addParameter(PARAMETER_USERNAME, username)
                    .addParameter(PARAMETER_FILE_POSTFIX, filePostfix)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-Type", "application/json");

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        try {
            return httpClient.execute(
                    request,
                    response -> {
                        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                        return objectMapper.readValue(responseString, StorageServiceDownloadResponse.class);
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String username, String filePostfix) {
        URI uri = null;
        try {
            uri = new URIBuilder(storageServiceConfig.getFileServicePath() + "/file")
                    .addParameter(PARAMETER_USERNAME, username)
                    .addParameter(PARAMETER_FILE_POSTFIX, filePostfix)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-Type", "application/json");

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        try {
            httpClient.execute(
                    request,
                    response -> {
                        return null;
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processFile(String username, String filePostfix) {
        HashMap<String, String> values = new HashMap<>() {{
            put(PARAMETER_USERNAME, username);
            put(PARAMETER_FILE_POSTFIX, filePostfix);
        }};

        ObjectMapper objectMapper = new ObjectMapper();

        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpPost request = new HttpPost(URI.create(storageServiceConfig.getFileServicePath() + "/file/process"));
        request.setHeader("Content-Type", "application/json");

        try {
            request.setEntity(new ByteArrayEntity(requestBody.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        try {
            httpClient.execute(
                    request,
                    response -> {
                        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                        return objectMapper.readValue(responseString, StorageServiceUploadResponse.class);
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
