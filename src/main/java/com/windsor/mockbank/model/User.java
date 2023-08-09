package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    @JsonProperty("user_id")
    @Schema(example = "1")
    private Integer userId;

    @JsonProperty("user_key")
    @Schema(description = "Unique identifier of the user",
            example = "P-0123456789")
    private String userKey;

    @Schema(example = "Bearer eyJhbGciOiJIUz....")
    private String token;

    @Schema(example = "test@gmail.com")
    private String email;

    @JsonIgnore
    @Schema(example = "Ywi9L8DhUnvU1npB")
    private String password;

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2023-08-09 18:28:20")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2023-08-09 18:28:20")
    private Date lastModifiedDate;
}
