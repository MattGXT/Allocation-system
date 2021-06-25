package com.example.demo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;


@Data
@Setter
@Getter
@Entity
@Table(name = "user_table")
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(64)")
    private String name;

    @Column(name = "sid", columnDefinition = "varchar(512)")
    private String sid;

    @Column(name = "unikey", columnDefinition = "varchar(512)")
    private String unikey;

    @Column(name = "role", columnDefinition = "varchar(32)")
    private String role;

    @Column(name = "account_email", columnDefinition = "varchar(64)")
    private String accountEmail;

    @Column(name = "password", columnDefinition = "varchar(64)")
    private String password;

    @Column(name = "token", columnDefinition = "varchar(512)")
    private String token;
}
