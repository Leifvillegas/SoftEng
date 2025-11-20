package com.example.tripto.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trip_participants")
@Data
public class TripParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
