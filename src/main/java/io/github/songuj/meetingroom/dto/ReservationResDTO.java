package io.github.songuj.meetingroom.dto;

import java.time.LocalDateTime;

import io.github.songuj.meetingroom.entity.Reservation;

public record ReservationResDTO(
    Long id,
    String room,
    String reservedBy,
    LocalDateTime start,
    LocalDateTime end) {

    // ⚠️ getRoom().getName(): room은 LAZY라서 여기서 회의실 SELECT가 나간다 → 목록에서는 N+1의 씨앗
    public static ReservationResDTO from(Reservation reservation) {
        return new ReservationResDTO(reservation.getId(), reservation.getRoom().getName(), reservation.getReservedBy(), reservation.getStartAt(), reservation.getEndAt());
    }
}
