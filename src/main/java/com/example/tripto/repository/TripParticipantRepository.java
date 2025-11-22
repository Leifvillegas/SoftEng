package com.example.tripto.repository;

import com.example.tripto.model.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {

    List<TripParticipant> findByTripId(Long tripId);

    List<TripParticipant> findByUserId(Long userId);

    List<TripParticipant> findByUserIdAndAcceptedFalse (Long userId);

}
