package com.hideyoshi.backendportfolio.microservice.storageService.enums;

public enum FileTypeEnum {
    PNG("png"),
    JPEG("jpeg");


    private String fileExtension;

    private FileTypeEnum(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }
}
