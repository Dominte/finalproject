package com.example.finalproject.models;


import com.example.finalproject.utils.UserRole;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name="registration_code", nullable = false,unique = true)
    private String registrationCode;

    private UserRole role;

    private String password;

    private String username;

    //@ManyToOne(cascade = {CascadeType.ALL})
    //private Test currentTest;

    //@OneToMany(cascade = CascadeType.ALL)
    //private List<Answer> givenAnswers;

}
