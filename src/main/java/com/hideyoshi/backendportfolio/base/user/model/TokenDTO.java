package com.hideyoshi.backendportfolio.base.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO implements Serializable {

    @NotNull(message = "Invalid AccessToken. Please Authenticate first.")
    private String token;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expirationDate;

}
