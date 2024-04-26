package com.wolfcode.user.mngt.service.controller;

import com.wolfcode.user.mngt.service.dto.*;
import com.wolfcode.user.mngt.service.exception.UserNotFoundException;
import com.wolfcode.user.mngt.service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) throws UserNotFoundException {
        return authService.login(loginRequest);
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public String signUp(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.signUp(registerRequest);
        return "account Created successfully";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody ForgotPassword forgotPassword) throws UserNotFoundException {
        authService.initiatePasswordReset(forgotPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/{otp}")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPassword resetPassword, @PathVariable String otp) throws UserNotFoundException {
        authService.resetPassword(resetPassword, otp);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePassword changePassword,
                                                 Principal connectedUser) throws UserNotFoundException {
        authService.changePassword(changePassword, connectedUser);
        return ResponseEntity.ok("Success !");
    }
}
