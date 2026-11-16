package io.github.songuj.meetingroom.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationReqDTO(
    @NotBlank String room, 
    @NotBlank String reservedBy,
    @NotNull LocalDateTime start,
    @NotNull LocalDateTime end) {
    
}
