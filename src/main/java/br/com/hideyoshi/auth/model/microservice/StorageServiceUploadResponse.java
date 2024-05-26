package br.com.hideyoshi.auth.model.microservice;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceUploadResponse {

    @JsonProperty("signed_url")
    private String signedUrl;

    @JsonProperty("expires_in")
    private String expiresIn;

}
