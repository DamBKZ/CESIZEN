package com.cesizen.cesizen_back.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserResponse {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("pseudo")
    private String pseudo;

    @JsonProperty("role")
    private String role;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("createdAt")
    private String userCreatedAt;
}