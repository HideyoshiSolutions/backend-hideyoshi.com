package br.com.hideyoshi.auth.microservice.storageService.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceDownloadResponse {

    @JsonProperty("presigned_url")
    private String presignedUrl;

}
