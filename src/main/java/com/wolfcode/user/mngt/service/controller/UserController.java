package com.wolfcode.user.mngt.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolfcode.eventservice.dto.MyEvents;
import com.wolfcode.user.mngt.service.dto.ProfileResponse;
import com.wolfcode.user.mngt.service.dto.UpdateProfile;
import com.wolfcode.user.mngt.service.exception.UserNotFoundException;
import com.wolfcode.user.mngt.service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    public ProfileResponse viewMyProfile(Principal connectedUser) throws UserNotFoundException, JsonProcessingException {
        return userService.viewMyProfile(connectedUser);
    }

    @PutMapping("/update-profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UpdateProfile updateProfile, Principal connectedUser) throws UserNotFoundException {
        userService.updateProfile(connectedUser, updateProfile);
        return ResponseEntity.ok("Your profile has been updated");
    }

    @DeleteMapping("/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deactivateAccount(@RequestParam String password, Principal connectedUser) throws UserNotFoundException {
        userService.deactivateAccount(connectedUser, password);
        return ResponseEntity.ok("Account deactivated");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/myEvents")
    @ResponseStatus(HttpStatus.OK)
    public List<MyEvents> getMyEvents(Principal connectedUser){
        return userService.getMyEvents(connectedUser);
    }


  ///  --Email verification after register
    /* ============== USER ANALYTICS ================= */
    @GetMapping("/active-users")
    @PreAuthorize("hasRole('ADMIN')")
    public long getActiveUsers(){
        return userService.getActiveUsers();
    }

    @GetMapping("/login-today")
    @PreAuthorize("hasRole('ADMIN')")
    public long userLoginActivityToday(){
        return userService.userLoginActivityToday();
    }
}
