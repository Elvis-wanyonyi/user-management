package com.wolfcode.user.mngt.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue
    private Long id;
    private String token;
    @Enumerated(EnumType.STRING)
    private Tokentype tokenType;
    private boolean isExpired;
    private boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user",referencedColumnName = "email")
    private Users user;
}
