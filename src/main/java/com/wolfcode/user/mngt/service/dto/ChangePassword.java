package com.wolfcode.user.mngt.service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassword {
    private String currentPassword;
    @Size(min = 6, message = "Password should be a minimum of 6 characters")
    private String newPassword;
    private String confirmNewPassword;
}
