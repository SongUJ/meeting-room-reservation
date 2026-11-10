package io.github.songuj.meetingroom.entity;

import java.time.LocalDateTime;

public record Reservation(Long id,String room, String reserveBy, LocalDateTime start, LocalDateTime end) {

    public Reservation withId(Long id) {
        return new Reservation(id,room,reserveBy,start,end);
    }
}
