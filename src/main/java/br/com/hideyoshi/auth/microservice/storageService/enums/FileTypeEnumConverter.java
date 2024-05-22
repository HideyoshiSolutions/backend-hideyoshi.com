package br.com.hideyoshi.auth.microservice.storageService.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FileTypeEnumConverter implements Converter<String, FileTypeEnum> {
    @Override
    public FileTypeEnum convert(String value) {
        return FileTypeEnum.fromValue(value);
    }
}