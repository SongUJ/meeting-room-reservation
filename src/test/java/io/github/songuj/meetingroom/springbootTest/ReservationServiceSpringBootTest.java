package io.github.songuj.meetingroom.springbootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.entity.Room;
import io.github.songuj.meetingroom.repository.RoomRepository;
import io.github.songuj.meetingroom.service.ReservationService;

// ⚠️ 실제 MySQL(meetingroom)에 붙는 테스트. DB가 떠 있고 테이블이 만들어져 있어야 한다 (README 7-4)
@SpringBootTest
@Transactional   // 테스트가 끝나면 롤백 → 테스트용 회의실·예약이 DB에 남지 않는다
class ReservationServiceSpringBootTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    RoomRepository roomRepository;

    LocalDateTime ten    = LocalDateTime.of(2026, 11, 13, 10, 0);
    LocalDateTime eleven = LocalDateTime.of(2026, 11, 13, 11, 0);

    @Test
    void 예약하고_번호로_찾으면_같은_예약이_나온다() {
        // given - 테스트용 회의실 (이름은 unique라 기초 데이터와 겹치지 않게)
        Room room = roomRepository.save(new Room("테스트회의실", 4));

        // when
        Reservation reserved = reservationService.reserve(room.getId(), "홍길동", ten, eleven);
        Reservation found = reservationService.findById(reserved.getId());

        // then
        assertEquals("테스트회의실", found.getRoom().getName());
        assertEquals("홍길동", found.getReservedBy());
    }
}
