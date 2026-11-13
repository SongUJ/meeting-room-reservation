package io.github.songuj.meetingroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation reserve(String room, String reservedBy, LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {   // 종료가 시작보다 뒤가 아니면(거꾸로이거나 같으면) 거부
            throw new IllegalArgumentException("종료 시각이 시작 시각보다 뒤여야 합니다.");
        }

        boolean overlap = reservationRepository.existsOverlap(room, start, end);

        if(overlap) {
            throw new IllegalStateException("이미 예약된 회의실 입니다.");
        }

        return reservationRepository.save(new Reservation(null, room, reservedBy, start, end));
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public void deleteById(Long id) {
        findById(id);                    // 없으면 여기서 NoSuchElementException이 나고 아래 줄은 실행 안 됨
        reservationRepository.deleteById(id);
    }

}
