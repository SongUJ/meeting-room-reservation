package io.github.songuj.meetingroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.songuj.meetingroom.entity.Reservation;
import io.github.songuj.meetingroom.entity.Room;
import io.github.songuj.meetingroom.repository.ReservationRepository;
import io.github.songuj.meetingroom.repository.RoomRepository;

@Service
@Transactional(readOnly = true)              // 클래스 기본값: 조회용 (스냅샷·flush 없음)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional                           // 쓰는 메서드만 readOnly를 덮어씀
    public Reservation reserve(Long roomId, String reservedBy, LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {   // 종료가 시작보다 뒤가 아니면(거꾸로이거나 같으면) 거부
            throw new IllegalArgumentException("종료 시각이 시작 시각보다 뒤여야 합니다.");
        }

        // 요청에 들어온 회의실 id가 DB에 없으면 → 일단 400 (README 7-1 질문: 400? 404? 직접 정하기)
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의실입니다. id=" + roomId));

        boolean overlap = reservationRepository.existsOverlap(roomId, start, end);

        if(overlap) {
            throw new IllegalStateException("이미 예약된 회의실 입니다.");
        }

        return reservationRepository.save(new Reservation(room, reservedBy, start, end));
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id);                    // 없으면 여기서 NoSuchElementException이 나고 아래 줄은 실행 안 됨
        reservationRepository.deleteById(id);
    }

}
