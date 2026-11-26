package io.github.songuj.meetingroom.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name="reservation")
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // optional = false
    // DDL: room_id NOT NULL (회의실 없는 예약은 저장 불가)
    // 조회: Hibernate가 JOIN할 때 inner join 사용 (true면 left outer join) FetchType이 EAGER이거나 Hibernate가 자동으로 JOIN할 때만 적용
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 50)
    private String reservedBy;

    @Column(name="start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name="end_at", nullable = false)
    private LocalDateTime endAt;

    public Reservation(Room room, String reservedBy, LocalDateTime startAt, LocalDateTime endAt) {
        this.room = room;
        this.reservedBy = reservedBy;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
