package br.com.hideyoshi.auth.microservice.storageservice.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceUploadResponse {

    @JsonProperty("presigned_url")
    private String presignedUrl;

    @JsonProperty("file_key")
    private String fileKey;

}
