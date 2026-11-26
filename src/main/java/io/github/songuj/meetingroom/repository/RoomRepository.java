package io.github.songuj.meetingroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.songuj.meetingroom.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // 비어 있어도 된다. findById(Long)는 JpaRepository가 이미 갖고 있다
}
