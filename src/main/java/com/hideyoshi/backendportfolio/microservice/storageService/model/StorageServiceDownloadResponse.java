package com.hideyoshi.backendportfolio.microservice.storageService.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceDownloadResponse {

    @JsonProperty("presigned_url")
    private String presignedUrl;

}
