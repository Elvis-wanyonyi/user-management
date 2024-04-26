package com.wolfcode.user.mngt.service.service;

import com.wolfcode.user.mngt.service.config.JwtService;
import com.wolfcode.user.mngt.service.dto.*;
import com.wolfcode.user.mngt.service.entity.Roles;
import com.wolfcode.user.mngt.service.entity.Token;
import com.wolfcode.user.mngt.service.entity.Tokentype;
import com.wolfcode.user.mngt.service.entity.Users;
import com.wolfcode.user.mngt.service.exception.UserNotFoundException;
import com.wolfcode.user.mngt.service.feignClients.EmailClient;
import com.wolfcode.user.mngt.service.repository.TokenRepository;
import com.wolfcode.user.mngt.service.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {


    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailClient emailClient;


    public AuthResponse login(@Valid LoginRequest authRequest) throws UserNotFoundException {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        var optionalUsers = userRepository.findByEmailIgnoreCase(authRequest.getEmail());
        if (optionalUsers.isPresent()) {

            Users user = optionalUsers.get();
            var jwtToken = jwtService.generateToken(user);
            revokeUserTokens(user);

            LocalDate today = LocalDate.now();
            if (user.getLastLogin() == null || user.getLastLogin().toLocalDate().isBefore(today)) {
                user.setTotalLogins(0);
            }
            user.setLastLogin(LocalDateTime.now());
            user.setTotalLogins(user.getTotalLogins() +1);

            userRepository.save(user);

            var token = Token.builder()
                    .token(jwtToken)
                    .tokenType(Tokentype.BEARER)
                    .isExpired(false)
                    .isRevoked(false)
                    .user(user)
                    .build();
            tokenRepository.save(token);
            log.info("{}:  has login  {} times today", user.getEmail(), user.getTotalLogins());

            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public void signUp(RegisterRequest registerRequest) {
        var user = Users.builder()
                .userCode("USER" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getCountryCode() + registerRequest.getPhone())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Roles.USER)
                .build();
        userRepository.save(user);
        log.info("new user: {} has joined", user.getEmail());
    }

    private void revokeUserTokens(Users user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getEmail());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Transactional
    public void changePassword(ChangePassword changePassword, Principal connectedUser) throws UserNotFoundException {

        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(changePassword.getCurrentPassword(), user.getPassword())) {
            throw new UserNotFoundException("Incorrect password");
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmNewPassword())) {
            throw new UserNotFoundException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userRepository.save(user);
        log.info("user {} has changed password ", user.getEmail());
    }


    @Scheduled(cron = "0 0 6 * * *")
    public void deleteExpiredAndRevokedTokens() {
        List<Token> tokens = tokenRepository.findAll();
        for (Token token : tokens) {
            if (token.isExpired() || token.isRevoked()) {
                tokenRepository.delete(token);
                log.info("Cleared all revoked and expired tokens");
            }
        }
    }

    public void initiatePasswordReset(ForgotPassword forgotPassword) throws UserNotFoundException {
        Users user = userRepository.findByEmailIgnoreCase(forgotPassword.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String otp = (UUID.randomUUID().toString().substring(0, 6));
        user.setOtp(otp);
        user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        String from = "Event Connect";
        String subject = "Password Reset for Your Account on Event Connect";
        String body = "Use this OTP to reset your password: " + otp + "\n Expires in 30 minutes";
        emailClient.sendEmail(new EmailRequest(from, user.getEmail(), subject, body));

    }

    public void resetPassword(ResetPassword resetPassword, String otp) throws UserNotFoundException {
        Users user = userRepository.findByOtp(otp)
                .orElseThrow(() -> new UserNotFoundException("Invalid reset code"));

        if (LocalDateTime.now().isAfter(user.getPasswordResetExpiry())) {
            throw new UserNotFoundException("Reset code expired");
        }

        validateNewPasswords(resetPassword.getNewPassword(), resetPassword.getConfirmPassword());

        user.setPassword(passwordEncoder.encode(resetPassword.getNewPassword()));
        user.setOtp(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    private void validateNewPasswords(String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isBlank() || !newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New passwords do not match");
        }
    }

}
