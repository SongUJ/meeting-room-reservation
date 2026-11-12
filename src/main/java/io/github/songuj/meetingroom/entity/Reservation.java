package io.github.songuj.meetingroom.entity;

import java.time.LocalDateTime;

public record Reservation(Long id,String room, String reservedBy, LocalDateTime start, LocalDateTime end) {

    public Reservation withId(Long id) {
        return new Reservation(id,room,reservedBy,start,end);
    }
}
