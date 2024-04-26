package com.wolfcode.user.mngt.service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Enter your full name")
    private String fullName;
    @NotBlank(message = "email is required")
    @Email(message = "enter a valid email")
    private String email;
    @NotBlank
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "password should be a minimum 6 characters")
    private String password;
    @Pattern(regexp = "^\\d{9}$", message = "Enter a valid phone number")
    private String phone;
    @NotEmpty(message = "enter your country code")
    private String countryCode;

}
