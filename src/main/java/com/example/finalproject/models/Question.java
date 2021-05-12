package com.example.finalproject.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "questions")
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_index")
    private int questionIndex;

    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    private Test test;

    //@OneToMany(cascade = CascadeType.ALL)
    //private List<Answer> answers;
}
