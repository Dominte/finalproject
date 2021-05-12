package com.example.finalproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tests")
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<User> students;

    @ManyToOne(cascade = CascadeType.ALL)
    private User teacher;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Question> questions;

    @NotNull
    private String title;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "test_date")
    private LocalDate testDate;

}
