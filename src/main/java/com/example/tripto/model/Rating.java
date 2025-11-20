package com.example.tripto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Fallback;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User rater;

    @ManyToOne
    private User ratedUser;

    @ManyToOne
    private Trip trip;

    private int score;

    private String comment;

    private boolean successful;

}
