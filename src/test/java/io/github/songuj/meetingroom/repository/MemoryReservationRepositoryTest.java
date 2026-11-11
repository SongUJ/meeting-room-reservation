package io.github.songuj.meetingroom.repository;

import java.time.LocalDateTime;

class MemoryReservationRepositoryTest {

    // 테스트에서 쓸 시각들. 전부 2026-11-11 하루 안이라 날짜는 신경 쓸 필요가 없다.
    // 필드로 빼 두면 테스트마다 LocalDateTime.of(...)를 다시 쓰지 않아도 된다.
    LocalDateTime nine       = LocalDateTime.of(2026, 11, 11,  9, 0);
    LocalDateTime ten        = LocalDateTime.of(2026, 11, 11, 10, 0);
    LocalDateTime tenHalf    = LocalDateTime.of(2026, 11, 11, 10, 30);
    LocalDateTime eleven     = LocalDateTime.of(2026, 11, 11, 11, 0);
    LocalDateTime elevenHalf = LocalDateTime.of(2026, 11, 11, 11, 30);
    LocalDateTime noon       = LocalDateTime.of(2026, 11, 11, 12, 0);

}
