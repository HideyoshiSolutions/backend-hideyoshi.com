package br.com.hideyoshi.auth.microservice.storageservice.service;

import br.com.hideyoshi.auth.service.microservice.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.hideyoshi.auth.config.StorageServiceConfig;
import br.com.hideyoshi.auth.enums.FileTypeEnum;
import br.com.hideyoshi.auth.model.microservice.StorageServiceDownloadResponse;
import br.com.hideyoshi.auth.model.microservice.StorageServiceUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StorageServiceTest {

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        StorageServiceConfig config = new StorageServiceConfig();

        this.storageService = new StorageService(new ObjectMapper(), config);
    }

    @Test
    void testGetNewFileUrlIfFileExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";
        FileTypeEnum fileTypeEnum = FileTypeEnum.JPEG;

        // When
        try {
            String responseString = "{\"presigned_url\":\"https://test.com\", \"file_key\":\"test\"}";
            Mockito.doReturn(responseString).when(storageService).postRequest(Mockito.any(), Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        var response = storageService.getNewFileUrl(username, filePostfix, fileTypeEnum);

        assertThat(response).isPresent();
        assertThat(response.get()).isInstanceOf(StorageServiceUploadResponse.class);
    }

    @Test
    void testGetNewFileUrlIfFileDoesNotExist() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";
        FileTypeEnum fileTypeEnum = FileTypeEnum.JPEG;

        // When
        try {
            Mockito.doThrow(new IOException()).when(storageService).postRequest(Mockito.any(), Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        var response = storageService.getNewFileUrl(username, filePostfix, fileTypeEnum);

        assertThat(response).isNotPresent();
    }

    @Test
    void getFileUrlIfExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";

        // When
        try {
            String responseString = "{\"presigned_url\":\"http://test.com\"}";
            Mockito.doReturn(responseString).when(storageService).getRequest(Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        var response = storageService.getFileUrl(username, filePostfix);

        assertThat(response).isPresent();
        assertThat(response.get()).isInstanceOf(StorageServiceDownloadResponse.class);
    }

    @Test
    void getFileUrlIfNotExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";

        // When
        try {
            Mockito.doThrow(new IOException()).when(storageService).getRequest(Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        var response = storageService.getFileUrl(username, filePostfix);

        assertThat(response).isNotPresent();
    }

    @Test
    void deleteFileIfExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";

        // When
        try {
            Mockito.doNothing().when(storageService).deleteRequest(Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        storageService.deleteFile(username, filePostfix);
    }

    @Test
    void deleteFileIfNotExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";

        // When
        try {
            Mockito.doThrow(new IOException()).when(storageService).deleteRequest(Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        storageService.deleteFile(username, filePostfix);
    }

    @Test
    void processFileIfExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";

        // When
        try {
            Mockito.doNothing().when(storageService).deleteRequest(Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        storageService.deleteFile(username, filePostfix);
    }

    @Test
    void processFileIfNotExists() {
        StorageService storageService = Mockito.spy(this.storageService);

        // Given
        String username = "test";
        String filePostfix = "test";

        // When
        try {
            Mockito.doThrow(new IOException()).when(storageService).deleteRequest(Mockito.any());
        } catch (IOException e) {
            assert false;
        }

        // Then
        storageService.deleteFile(username, filePostfix);
    }

}
