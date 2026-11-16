package io.github.songuj.meetingroom.dto;

import java.time.LocalDateTime;

import io.github.songuj.meetingroom.entity.Reservation;

public record ReservationResDTO(
    Long id,
    String room,
    String reservedBy,
    LocalDateTime start,
    LocalDateTime end) {
    
    public static ReservationResDTO from(Reservation reservation) {
        return new ReservationResDTO(reservation.id(), reservation.room(), reservation.reservedBy(), reservation.start(), reservation.end());
    }
}
