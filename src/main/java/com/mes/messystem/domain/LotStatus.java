package com.mes.messystem.domain;

public enum LotStatus {
    CREATED,        // LOT 생성됨
    IN_PROGRESS,    // 생산 진행 중
    COMPLETED,      // 생산 완료
    ON_HOLD,        // 대기 중
    REJECTED,       // 불합격
    SHIPPED         // 출하됨
}
