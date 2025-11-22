package com.example.tripto.repository;

import com.example.tripto.model.TripInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {

    List<TripInvitation> findByToUserIdAndStatus(Long toUserId, TripInvitation.Status status);

}
