package br.com.hideyoshi.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class StorageServiceConfig {
    @Value("${br.com.hideyoshi.microservice.storageServicePath}")
    private String fileServicePath;
}
