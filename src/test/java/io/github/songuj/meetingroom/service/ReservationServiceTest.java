package io.github.songuj.meetingroom.service;

import static io.github.songuj.meetingroom.TestFixtures.reservation;
import static io.github.songuj.meetingroom.TestFixtures.room;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.entity.Room;
import io.github.songuj.meetingroom.repository.ReservationRepository;
import io.github.songuj.meetingroom.repository.RoomRepository;

class ReservationServiceTest {
    // 테스트에 쓸 시각 3개 (05와 같은 방식)
    LocalDateTime ten    = LocalDateTime.of(2026, 11, 13, 10, 0);
    LocalDateTime eleven = LocalDateTime.of(2026, 11, 13, 11, 0);

    Room roomA = room(1L, "A회의실");
    Reservation saved = reservation(1L, roomA, "홍길동", ten, eleven);

    ReservationRepository reservationRepository;
    RoomRepository roomRepository;
    ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        roomRepository = mock(RoomRepository.class);
        reservationService = new ReservationService(reservationRepository, roomRepository);
    }

    @Test
    void 예약하면_저장소가_붙인_번호가_돌아온다() {
        // given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomA));
        when(reservationRepository.existsOverlap(1L, ten, eleven)).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(saved);
        //given에서 return 값으로 셋팅후 when reserve를 호출한다.

        // when
        Reservation result = reservationService.reserve(1L, "홍길동", ten, eleven);

        // then
        assertEquals(1L, result.getId());
        //times(2) 정확히 2번, never() 0번
        verify(reservationRepository).save(any()); // 정확히 1번
    }

    @Test
    void 없는_회의실이면_예외가_나고_저장하지_않는다() {
        // given
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.reserve(99L, "홍길동", ten, eleven));

        // then
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void 기간이_거꾸로면_예외가_나고_저장소를_부르지_않는다() {
        // when + then
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.reserve(1L, "홍길동", eleven, ten));

        // then
        verifyNoInteractions(reservationRepository); // 0번(모든 메서드 검사)
    }

    @Test
    void 시작과_종료가_같으면_예외() {
        // when + then
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.reserve(1L, "홍길동", ten, ten));

        verifyNoInteractions(reservationRepository); // 0번(모든 메서드 검사)
    }

    @Test
    void 겹치면_예외가_나고_저장하지_않는다() {
        // given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomA));
        when(reservationRepository.existsOverlap(1L, ten, eleven)).thenReturn(true);

        // when + then
        assertThrows(IllegalStateException.class,
                () -> reservationService.reserve(1L, "홍길동", ten, eleven));

        // then
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void 있는_번호로_조회하면_그_예약이_나온다() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(saved));

        // when
        Reservation result = reservationService.findById(1L);

        // then
        assertEquals(saved, result);
    }

    @Test
    void 없는_번호로_조회하면_예외() {
        // given
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(NoSuchElementException.class, () -> reservationService.findById(99L));
    }

    @Test
    void 있는_번호를_취소하면_삭제된다() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(saved));

        // when
        reservationService.deleteById(1L);

        // then
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void 없는_번호를_취소하면_예외가_나고_삭제하지_않는다() {
        // given
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(NoSuchElementException.class, () -> reservationService.deleteById(99L));

        // then
        verify(reservationRepository, never()).deleteById(any());
    }
}
