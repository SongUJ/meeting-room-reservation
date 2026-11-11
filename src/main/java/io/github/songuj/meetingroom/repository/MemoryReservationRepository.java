package io.github.songuj.meetingroom.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import io.github.songuj.meetingroom.entity.Reservation;

@Repository 
public class MemoryReservationRepository implements ReservationRepository{

    private final Map<Long, Reservation> map = new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong();

    @Override 
    public Reservation save(Reservation reservation) {
        long id = sequence.incrementAndGet();

        Reservation saved = reservation.withId(id);

        map.put(id, saved);

        return saved;
    }

}
