package io.github.songuj.meetingroom.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.github.songuj.meetingroom.entity.Reservation;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor       // ← final 필드(jpa)를 받는 생성자를 Lombok이 만들어 줌 (생성자 주입)
public class JpaReservationRepository implements ReservationRepository {

    private final ReservationJpaRepository jpa;

    @Override
    public Reservation save(Reservation reservation) {
        return jpa.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Reservation> findAll() {
        return jpa.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    // 겹침 조건: 기존 시작 < 요청 끝 AND 기존 끝 > 요청 시작
    // 메서드 이름의 조건 순서(StartAtLessThan → EndAtGreaterThan)대로 값이 들어가서 end, start 순서로 넘긴다
    @Override
    public boolean existsOverlap(Long roomId, LocalDateTime start, LocalDateTime end) {
        return jpa.existsByRoomIdAndStartAtLessThanAndEndAtGreaterThan(roomId, end, start);
    }
}
