package br.com.hideyoshi.auth.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.hideyoshi.auth.util.exception.BadRequestException;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum FileTypeEnum {
    PNG("png"),

    JPEG("jpeg");


    private final String fileExtension;

    FileTypeEnum(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static FileTypeEnum fromValue(String value) {
        for (FileTypeEnum e : FileTypeEnum.values()) {
            if (e.getFileExtension().equals(value)) {
                return e;
            }
        }
        throw new BadRequestException("Invalid FileType.");
    }

}
