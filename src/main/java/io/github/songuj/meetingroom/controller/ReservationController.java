package io.github.songuj.meetingroom.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.songuj.meetingroom.dto.ReservationReqDTO;
import io.github.songuj.meetingroom.dto.ReservationResDTO;
import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.service.ReservationService;
import jakarta.validation.Valid;

// @RestController	return한 객체를 JSON으로 바꿔 응답 본문에 넣음
// @RequestMapping("/reservations")	클래스에 붙임. URL 앞부분
// @PostMapping / @GetMapping / @DeleteMapping	받을 HTTP 메서드. 괄호 안에 URL 뒷부분("/{id}")
// @RequestBody	요청 본문 JSON을 이 매개변수 객체로 바꿈
// @Valid	바꾼 뒤 ⑧의 @NotBlank/@NotNull을 검사함. 없으면 검사 안 해요
// @PathVariable	URL의 {id} 값을 매개변수에 넣음 (/reservations/3 → id = 3)
// @ResponseStatus(HttpStatus.NO_CONTENT)	성공하면 200 대신 204로 응답

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping 
    public ResponseEntity<ReservationResDTO> reserve(@Valid @RequestBody ReservationReqDTO reservationReqDTO) {
        // 1. request의 값 4개로 서비스 reserve 호출
        Reservation saved = reservationService.reserve(reservationReqDTO.roomId(), reservationReqDTO.reservedBy(), reservationReqDTO.start(), reservationReqDTO.end());

        // 2. return ResponseEntity.created(URI.create("/reservations/" + 번호)).body(응답 DTO);
        return ResponseEntity.created(URI.create("/reservations/" + saved.getId())).body(ReservationResDTO.from(saved));
    }

    @GetMapping("/{id}")
    public ReservationResDTO findById(@PathVariable Long id) {
        Reservation reservation = reservationService.findById(id);

        return ReservationResDTO.from(reservation);
        // return ReservationResDTO.from(reservationService.findById(id));
    }

    @GetMapping 
    public List<ReservationResDTO> findAll() {
        // List<Reservation> → List<ReservationResDTO>: 스트림 map
        List<Reservation> reservations = reservationService.findAll();

        return reservations.stream()
                .map(r -> ReservationResDTO.from(r))
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        reservationService.deleteById(id);
    }

}
