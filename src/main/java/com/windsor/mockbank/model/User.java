package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.windsor.mockbank.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class User implements UserDetails {

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

    private Role role;

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2023-08-09 18:28:20")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2023-08-09 18:28:20")
    private Date lastModifiedDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userKey;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
