package com.wolfcode.user.mngt.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.eventservice.dto.MyEvents;
import com.wolfcode.user.mngt.service.dto.ProfileResponse;
import com.wolfcode.user.mngt.service.dto.UpdateProfile;
import com.wolfcode.user.mngt.service.entity.Users;
import com.wolfcode.user.mngt.service.exception.UserNotFoundException;
import com.wolfcode.user.mngt.service.feignClients.EventsClient;
import com.wolfcode.user.mngt.service.kafka.ProducerConfig;
import com.wolfcode.user.mngt.service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ProducerConfig kafkaProducerConfig;
    private final PasswordEncoder passwordEncoder;
    private final EventsClient eventsClient;



    public ProfileResponse viewMyProfile(Principal connectedUser) throws UserNotFoundException, JsonProcessingException {
        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        String userJson = objectMapper.writeValueAsString(user);
        log.debug(String.format("my profile ======= %s", userJson));
        return objectMapper.readValue(userJson, ProfileResponse.class);

    }

    public void updateProfile(Principal connectedUser, UpdateProfile updateProfile) throws UserNotFoundException {
        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        user.setFullName(updateProfile.getFullName());
        user.setUsername(updateProfile.getUsername());
        user.setPhone(updateProfile.getPhone());

        userRepository.save(user);
    }

    @Transactional
    public void deactivateAccount(Principal connectedUser, String password) throws UserNotFoundException {
        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException("Incorrect password");
        }

        String email = user.getEmail();
        userRepository.deleteByEmail(email);

        kafkaProducerConfig.sendUserDeactivatePayload(email);
        log.info("user {} has deactivated ", user.getEmail());
    }

    public long getActiveUsers() {
        return userRepository.findAll().size();
    }

    public long userLoginActivityToday() {
        return userRepository.findAll()
                .stream()
                .mapToLong(Users::getTotalLogins)
                .sum();
    }

    public List<MyEvents> getMyEvents(Principal connectedUser) {
        Users user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        Optional<Users> optionalUsers = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (optionalUsers.isEmpty()){
            return Collections.emptyList();
        }

        Users users = optionalUsers.get();
       return eventsClient.getEventByOrganizer(users.getEmail());

    }
}
