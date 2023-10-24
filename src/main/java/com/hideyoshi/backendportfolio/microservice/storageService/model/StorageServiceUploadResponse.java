package com.hideyoshi.backendportfolio.microservice.storageService.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceUploadResponse {

    @JsonProperty("presigned_url")
    private String presignedUrl;

    @JsonProperty("file_key")
    private String fileKey;

}
