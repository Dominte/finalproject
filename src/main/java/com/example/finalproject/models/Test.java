package com.example.finalproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> students;

    @ManyToOne(cascade = CascadeType.ALL)
    private User teacher;

    @NotNull
    private String title;

    @JsonFormat(pattern = "HH:mm")
    @Column(name= "starting_hour")
    private LocalTime startingHour;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "finish_hour")
    private LocalTime duration;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "test_date")
    private LocalDate testDate;

}
