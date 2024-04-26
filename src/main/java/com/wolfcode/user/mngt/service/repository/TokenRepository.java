package com.wolfcode.user.mngt.service.repository;

import com.wolfcode.user.mngt.service.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {


    @Query("select t from Token t inner join Users u on t.user.email = u.email where" +
            " u.email = :user and (t.isExpired = false or t.isRevoked = false)")
    List<Token> findAllValidTokensByUser(String user);


    Optional<Token> findByToken(String token);
}
