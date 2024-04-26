package com.wolfcode.user.mngt.service.repository;

import com.wolfcode.user.mngt.service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {


    Optional<Users> findByEmailIgnoreCase(String email);

    void deleteByEmail(String email);

    Optional<Users> findByOtp(String otp);

    long countByLastLoginBetween(LocalDateTime todayStart, LocalDateTime tomorrowStart);
}
