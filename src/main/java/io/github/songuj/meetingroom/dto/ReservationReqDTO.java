package io.github.songuj.meetingroom.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationReqDTO(
    @NotNull Long roomId,               // 회의실은 이름이 아니라 id로 받는다 (@NotBlank는 문자열 전용이라 @NotNull)
    @NotBlank String reservedBy,
    @NotNull LocalDateTime start,
    @NotNull LocalDateTime end) {

}
