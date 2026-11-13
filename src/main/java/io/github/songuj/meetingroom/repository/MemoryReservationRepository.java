package io.github.songuj.meetingroom.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override 
    public List<Reservation> findAll() {
        Collection<Reservation> reservations = map.values();

        return reservations.stream().toList();
    }
    
    @Override 
    public void deleteById(Long id) {
        map.remove(id);
    }

    // 회의실 예약 겹치면 true
    @Override 
    public boolean existsOverlap(String room, LocalDateTime start, LocalDateTime end) {
        return map.values().stream()
                .filter(r -> r.room().equals(room))
                .anyMatch(r -> start.isBefore(r.end()) && end.isAfter(r.start()));
    }

}
