package com.example.tripto.repository;

import com.example.tripto.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRatedUserId(Long ratedUserId);
    List<Rating> findByRaterId(Long raterId);

}
