package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    @JsonIgnore
    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("user_key")
    private String userKey;

    private String token;
    private String email;

    @JsonIgnore
    private String password;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;
}
