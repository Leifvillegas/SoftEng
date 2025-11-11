package com.example.tripto.controller;

import com.example.tripto.model.Trip;
import com.example.tripto.repository.DestinationRepository;
import com.example.tripto.repository.TripRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TripApiController {

    private final TripRepository tripRepo;
    private final DestinationRepository destRepo;

    public TripApiController(TripRepository tripRepo, DestinationRepository destRepo) {
        this.tripRepo = tripRepo;
        this.destRepo = destRepo;
    }

    @GetMapping("/trips")
    public List<Trip> getTrips() {
        return tripRepo.findAll();
    }

    @PostMapping("/trips")
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip trip) {
        if(trip.getDestination() !=null && trip.getDestination().getId() != null) {
            destRepo.findById(trip.getDestination().getId()).ifPresent(trip::setDestination);
        }
        Trip saved = tripRepo.save(trip);
        return ResponseEntity.created(URI.create("/api/trips/" + saved.getId())).body(saved);
    }

    @GetMapping("/trips/{id}")
    public ResponseEntity<Trip> getTrip(@PathVariable Long id) {
        return tripRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/trips/{id}")
    public ResponseEntity<Trip> deleteTrip(@PathVariable Long id) {
        if (!tripRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tripRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/destinations")
    public ResponseEntity<?> getDestinations() {
        return ResponseEntity.ok(destRepo.findAll());
    }

}
