package io.github.songuj.meetingroom.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.service.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReservationService reservationService;

    String body = """
            {"room":"A회의실","reservedBy":"홍길동","start":"2026-11-13T10:00:00","end":"2026-11-13T11:00:00"}
            """;

    // room만 빈 문자열, 나머지 3개는 정상
    String emptyRoomBody = """
            {"room":"","reservedBy":"홍길동","start":"2026-11-13T10:00:00","end":"2026-11-13T11:00:00"}
            """;

    // 종료(10:00)가 시작(11:00)보다 앞 - 형식은 정상이라 @Valid는 통과한다
    String reversedTimeBody = """
            {"room":"A회의실","reservedBy":"홍길동","start":"2026-11-13T11:00:00","end":"2026-11-13T10:00:00"}
            """;

    @Test
    void 예약하면_201과_번호를_준다() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2026, 11, 13, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 13, 11, 0);
        Reservation saved = new Reservation(1L, "A회의실", "홍길동", start, end);

        when(reservationService.reserve("A회의실", "홍길동", start, end)).thenReturn(saved);

        // when + then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservations/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room").value("A회의실"));
    }

    @Test
    void 회의실_이름이_비면_400이고_서비스를_부르지_않는다() throws Exception {
        // given 없음 - 서비스가 불릴 일이 없다

        // when + then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyRoomBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("room 값이 비어 있습니다."));

        // then - 잘못된 요청은 서비스까지 내려가지 않는다
        verify(reservationService, never()).reserve(any(), any(), any(), any());
    }

    @Test
    void 기간이_거꾸로면_400() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2026, 11, 13, 11, 0);   // 시작 11시
        LocalDateTime end   = LocalDateTime.of(2026, 11, 13, 10, 0);   // 종료 10시 (시작보다 앞 = 거꾸로)

        when(reservationService.reserve("A회의실", "홍길동", start, end))
                .thenThrow(new IllegalArgumentException("종료 시각이 시작 시각보다 뒤여야 합니다."));

        // when + then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reversedTimeBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("종료 시각이 시작 시각보다 뒤여야 합니다."));
    }

    @Test
    void 시간이_겹치면_409() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2026, 11, 13, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 13, 11, 0);

        when(reservationService.reserve("A회의실", "홍길동", start, end))
                .thenThrow(new IllegalStateException("이미 예약된 회의실 입니다."));

        // when + then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 예약된 회의실 입니다."));
    }

    @Test
    void 없는_번호로_조회하면_404() throws Exception {
        // given
        when(reservationService.findById(99L)).thenThrow(new NoSuchElementException());

        // when + then
        mockMvc.perform(get("/reservations/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("예약을 찾을 수 없습니다."));
    }

    @Test
    void 예약을_취소하면_204이고_서비스를_부른다() throws Exception {
        // given 없음 - deleteById는 void라서 아무것도 시킬 게 없다 (기본 동작: 아무 일도 안 함)

        // when + then
        mockMvc.perform(delete("/reservations/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        // then - 서비스에 삭제를 1번 시켰는지
        verify(reservationService, times(1)).deleteById(1L);
    }

    @Test
    void 없는_번호를_취소하면_404() throws Exception {
        // given - void 메서드는 doThrow(...).when(가짜).메서드(...) 순서로 뒤집어 쓴다
        doThrow(new NoSuchElementException()).when(reservationService).deleteById(99L);

        // when + then
        mockMvc.perform(delete("/reservations/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("예약을 찾을 수 없습니다."));
    }
}
