package io.github.songuj.meetingroom.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.songuj.meetingroom.entity.Reservation;

class MemoryReservationRepositoryTest {

    // 테스트에서 쓸 시각들. 전부 2026-11-11 하루 안이라 날짜는 신경 쓸 필요가 없다.
    // 필드로 빼 두면 테스트마다 LocalDateTime.of(...)를 다시 쓰지 않아도 된다.
    LocalDateTime nine       = LocalDateTime.of(2026, 11, 11,  9, 0);
    LocalDateTime ten        = LocalDateTime.of(2026, 11, 11, 10, 0);
    LocalDateTime tenHalf    = LocalDateTime.of(2026, 11, 11, 10, 30);
    LocalDateTime eleven     = LocalDateTime.of(2026, 11, 11, 11, 0);
    LocalDateTime elevenHalf = LocalDateTime.of(2026, 11, 11, 11, 30);
    LocalDateTime noon       = LocalDateTime.of(2026, 11, 11, 12, 0);

    MemoryReservationRepository repository;

    @BeforeEach 
    void setUp() {
        repository = new MemoryReservationRepository();
    }

    @Test 
    void 예약_테스트() {
        Reservation reservation = new Reservation(null, "A회의실", "홍길동", ten, eleven);

        Reservation saved = repository.save(reservation);

        assertNotNull(saved.id());
    }

    @Test 
    void 두번_저장하면_id가_다르다() {
        Reservation first = new Reservation(null, "A회의실", "FIRST", ten, eleven);
        Reservation second = new Reservation(null, "A회의실", "SECOND", ten, eleven);

        Reservation savedFirst = repository.save(first);
        Reservation savedSecond = repository.save(second);

        assertNotEquals(savedFirst.id(), savedSecond.id());
    }

    @Test
    void 저장한_걸_findById로_찾으면_같은_예약이_나온다() {
        Reservation reservation = new Reservation(null, "A회의실", "홍길동", ten, eleven);
        Reservation saved = repository.save(reservation);

        Optional<Reservation> found = repository.findById(saved.id());

        assertEquals(saved, found.get());
    }

    @Test 
    void 없는_번호로_findById하면_빈_Optional이다() {
        Optional<Reservation> found = repository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test 
    void deleteById_후_findById하면_빈_Optional이다() {
        Reservation reservation = new Reservation(null, "A회의실", "홍길동", ten, eleven);
        Reservation saved = repository.save(reservation);

        repository.deleteById(saved.id());

        Optional<Reservation> found = repository.findById(saved.id());

        assertTrue(found.isEmpty());
    }

    @Test 
    void 같은_회의실_시간이_겹치면_true() {
        repository.save(new Reservation(null, "A회의실", "홍길동", ten, eleven));

        boolean overlap = repository.existsOverlap("A회의실", tenHalf, elevenHalf);

        assertTrue(overlap);
    }

    @Test 
    void 끝나는_시각에_바로_시작하면_안_겹친다() {
        repository.save(new Reservation(null, "A회의실", "홍길동", ten, eleven));

        boolean overlap = repository.existsOverlap("A회의실", eleven, noon);

        assertFalse(overlap);
    }

    @Test 
    void 다른_회의실이면_시간이_같아도_안_겹친다() {
        repository.save(new Reservation(null, "A회의실", "홍길동", ten, eleven));

        boolean overlap = repository.existsOverlap("B회의실", ten, eleven);

        assertFalse(overlap);
    }
}
