package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Integer id;

    @JsonProperty("unique_id")
    private String uniqueId;
    private String email;

    @JsonIgnore
    private String password;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;
}
