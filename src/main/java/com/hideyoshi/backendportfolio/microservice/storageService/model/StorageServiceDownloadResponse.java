package com.hideyoshi.backendportfolio.microservice.storageService.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceDownloadResponse {

    @JsonProperty("presigned_url")
    private String presignedUrl;

}
