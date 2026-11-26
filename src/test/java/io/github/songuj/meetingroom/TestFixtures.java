package io.github.songuj.meetingroom;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.entity.Room;

// 테스트용 엔티티를 만드는 도우미.
// 엔티티는 id를 DB가 정해서 setter가 없다. DB 없이 "id가 이미 붙은 엔티티"가 필요할 때는
// ReflectionTestUtils(스프링 테스트 도구)로 private id 필드에 직접 값을 넣는다.
public final class TestFixtures {

    private TestFixtures() { }

    public static Room room(Long id, String name) {
        Room room = new Room(name, 10);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }

    public static Reservation reservation(Long id, Room room, String reservedBy,
                                          LocalDateTime start, LocalDateTime end) {
        Reservation reservation = new Reservation(room, reservedBy, start, end);
        ReflectionTestUtils.setField(reservation, "id", id);
        return reservation;
    }
}
