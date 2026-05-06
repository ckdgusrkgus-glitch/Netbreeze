package com.netbreeze.flathome.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 방문자 예약 엔티티
 */
@Entity
@Table(name = "visitors",
       indexes = @Index(columnList = "household_id, visit_date"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false, length = 50)
    private String visitorName;

    @Column(name = "car_plate", length = 20)
    private String carPlate;           // 차량번호 (없으면 null)

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitPurpose purpose;      // FAMILY, DELIVERY, REPAIR, ETC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;        // PENDING, APPROVED, REJECTED, EXPIRED

    @Column(name = "qr_token", unique = true, length = 100)
    private String qrToken;            // QR 출입증 토큰

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 팩토리 ──────────────────────────────
    public static Visitor create(Household household, String visitorName,
                                 String carPlate, LocalDate visitDate,
                                 VisitPurpose purpose) {
        Visitor v = new Visitor();
        v.household   = household;
        v.visitorName = visitorName;
        v.carPlate    = carPlate;
        v.visitDate   = visitDate;
        v.purpose     = purpose;
        v.status      = VisitStatus.PENDING;
        return v;
    }

    // ── 도메인 메서드 ─────────────────────────────
    public void approve(String qrToken) {
        if (this.status != VisitStatus.PENDING)
            throw new IllegalStateException("승인 가능한 상태가 아닙니다.");
        this.status  = VisitStatus.APPROVED;
        this.qrToken = qrToken;
    }

    public void reject() {
        this.status = VisitStatus.REJECTED;
    }

    public enum VisitPurpose { FAMILY, DELIVERY, REPAIR, ETC }
    public enum VisitStatus  { PENDING, APPROVED, REJECTED, EXPIRED }
}
