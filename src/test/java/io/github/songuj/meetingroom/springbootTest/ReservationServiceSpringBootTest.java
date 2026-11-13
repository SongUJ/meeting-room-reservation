package io.github.songuj.meetingroom.springbootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.service.ReservationService;

@SpringBootTest
class ReservationServiceSpringBootTest {

    @Autowired
    ReservationService reservationService;

    LocalDateTime ten    = LocalDateTime.of(2026, 11, 13, 10, 0);
    LocalDateTime eleven = LocalDateTime.of(2026, 11, 13, 11, 0);

    @Test
    void 예약하고_번호로_찾으면_같은_예약이_나온다() {
        // when
        Reservation reserved = reservationService.reserve("A회의실", "홍길동", ten, eleven);
        Reservation found = reservationService.findById(reserved.id());

        // then
        assertEquals("A회의실", found.room());
        assertEquals("홍길동", found.reservedBy());
    }
}
