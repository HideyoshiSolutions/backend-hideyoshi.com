package com.hideyoshi.backendportfolio.microservice.storageService.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class StorageServiceUploadResponse {

    @JsonProperty("presigned_url")
    private String presignedUrl;

    @JsonProperty("file_key")
    private String fileKey;

}
