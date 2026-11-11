package io.github.songuj.meetingroom.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import io.github.songuj.meetingroom.entity.Reservation;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    List<Reservation> findAll();

    void deleteById(Long id);

    boolean existsOverlap(String room, LocalDateTime start, LocalDateTime end);
}
