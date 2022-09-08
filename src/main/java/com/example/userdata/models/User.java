package com.example.userdata.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "exams")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "public_data", nullable = false)
    private String publicData;

    @Column(name = "secret_data", nullable = false)
    private String secretData;

    @Column(name = "password", nullable = false)
    private String passwordHash;
}