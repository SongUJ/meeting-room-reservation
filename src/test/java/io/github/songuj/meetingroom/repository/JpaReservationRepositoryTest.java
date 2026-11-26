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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.entity.Room;

// 저장소 계층만 띄우는 테스트 (컨트롤러·서비스는 안 뜸). 예전 MemoryReservationRepositoryTest를 옮긴 것
// - @DataJpaTest: JPA 관련 빈만 띄우고, 테스트마다 트랜잭션을 열었다가 끝나면 롤백 → DB에 데이터가 남지 않음
// - replace = NONE: 내장 DB(H2)로 바꾸지 말고 application.properties의 MySQL을 그대로 쓴다
//   (실제로 돌아갈 DB에서 메서드 이름 쿼리·겹침 조건이 맞는지 확인하려고)
// - @Import: @DataJpaTest는 Spring Data 저장소만 띄우고 우리가 만든 @Repository 클래스는 안 띄워서 직접 넣는다
// ⚠️ MySQL이 떠 있고 테이블이 만들어져 있어야 한다 (README 7-4)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaReservationRepository.class)
class JpaReservationRepositoryTest {

    @Autowired
    JpaReservationRepository repository;

    @Autowired
    TestEntityManager em;   // 테스트에서 회의실 같은 준비 데이터를 넣을 때 쓰는 도구

    LocalDateTime ten        = LocalDateTime.of(2026, 11, 11, 10, 0);
    LocalDateTime tenHalf    = LocalDateTime.of(2026, 11, 11, 10, 30);
    LocalDateTime eleven     = LocalDateTime.of(2026, 11, 11, 11, 0);
    LocalDateTime elevenHalf = LocalDateTime.of(2026, 11, 11, 11, 30);
    LocalDateTime noon       = LocalDateTime.of(2026, 11, 11, 12, 0);

    Room roomA;
    Room roomB;

    @BeforeEach
    void setUp() {
        // 회의실 이름은 unique라 기초 데이터(A회의실 등)와 겹치지 않는 이름을 쓴다. 테스트가 끝나면 롤백된다
        roomA = em.persist(new Room("테스트회의실A", 4));
        roomB = em.persist(new Room("테스트회의실B", 4));
    }

    @Test
    void 저장하면_DB가_id를_붙여_준다() {
        Reservation saved = repository.save(new Reservation(roomA, "홍길동", ten, eleven));

        assertNotNull(saved.getId());
    }

    @Test
    void 두번_저장하면_id가_다르다() {
        Reservation first = repository.save(new Reservation(roomA, "FIRST", ten, eleven));
        Reservation second = repository.save(new Reservation(roomA, "SECOND", eleven, noon));

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void 저장한_걸_findById로_찾으면_같은_예약이_나온다() {
        Reservation saved = repository.save(new Reservation(roomA, "홍길동", ten, eleven));
        em.flush();   // INSERT를 DB로 보냄
        em.clear();   // 영속성 컨텍스트를 비움 → 다음 조회가 1차 캐시가 아니라 DB에서 SELECT

        Optional<Reservation> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("홍길동", found.get().getReservedBy());
        assertEquals(roomA.getId(), found.get().getRoom().getId());
    }

    @Test
    void 없는_번호로_findById하면_빈_Optional이다() {
        Optional<Reservation> found = repository.findById(-1L);

        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_후_findById하면_빈_Optional이다() {
        Reservation saved = repository.save(new Reservation(roomA, "홍길동", ten, eleven));

        repository.deleteById(saved.getId());
        em.flush();
        em.clear();

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void 같은_회의실_시간이_겹치면_true() {
        repository.save(new Reservation(roomA, "홍길동", ten, eleven));

        assertTrue(repository.existsOverlap(roomA.getId(), tenHalf, elevenHalf));
    }

    @Test
    void 끝나는_시각에_바로_시작하면_안_겹친다() {
        repository.save(new Reservation(roomA, "홍길동", ten, eleven));

        assertFalse(repository.existsOverlap(roomA.getId(), eleven, noon));
    }

    @Test
    void 다른_회의실이면_시간이_같아도_안_겹친다() {
        repository.save(new Reservation(roomA, "홍길동", ten, eleven));

        assertFalse(repository.existsOverlap(roomB.getId(), ten, eleven));
    }
}
