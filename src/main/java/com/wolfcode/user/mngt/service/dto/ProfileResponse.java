package com.wolfcode.user.mngt.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileResponse(
        String userCode,
        String fullName,
        String email,
        String username,

        String phone
) {
}
