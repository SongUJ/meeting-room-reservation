package io.github.songuj.meetingroom.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.songuj.meetingroom.entity.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    
    boolean existsByRoomIdAndStartAtLessThanAndEndAtGreaterThan(Long roomId, LocalDateTime end, LocalDateTime start);

}
