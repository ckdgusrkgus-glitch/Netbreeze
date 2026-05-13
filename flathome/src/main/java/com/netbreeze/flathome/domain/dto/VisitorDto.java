package com.netbreeze.flathome.domain.dto;

import com.netbreeze.flathome.domain.entity.Visitor;
import com.netbreeze.flathome.domain.entity.Visitor.VisitPurpose;
import com.netbreeze.flathome.domain.entity.Visitor.VisitStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VisitorDto {

    // ── 등록 요청 ──────────────────────────────────
    @Getter
    public static class RegisterRequest {

        @NotBlank(message = "방문자 이름은 필수입니다.")
        @Size(max = 50)
        private String visitorName;

        @Size(max = 20, message = "차량번호는 20자 이내여야 합니다.")
        private String carPlate;           // 선택

        @NotNull(message = "방문 날짜는 필수입니다.")
        @FutureOrPresent(message = "방문 날짜는 오늘 이후여야 합니다.")
        private LocalDate visitDate;

        @NotNull(message = "방문 목적은 필수입니다.")
        private VisitPurpose purpose;
    }

    // ── 목록 응답 ──────────────────────────────────
    @Getter
    @Builder
    public static class Summary {
        private Long id;
        private String visitorName;
        private String carPlate;
        private LocalDate visitDate;
        private VisitPurpose purpose;
        private VisitStatus status;
        private LocalDateTime createdAt;

        public static Summary from(Visitor v) {
            return Summary.builder()
                    .id(v.getId())
                    .visitorName(v.getVisitorName())
                    .carPlate(v.getCarPlate())
                    .visitDate(v.getVisitDate())
                    .purpose(v.getPurpose())
                    .status(v.getStatus())
                    .createdAt(v.getCreatedAt())
                    .build();
        }
    }

    // ── 승인 응답 (QR 포함) ────────────────────────
    @Getter
    @Builder
    public static class ApproveResponse {
        private Long visitorId;
        private VisitStatus status;
        private String qrToken;        // Base64 인코딩된 QR 데이터

        public static ApproveResponse from(Visitor v) {
            return ApproveResponse.builder()
                    .visitorId(v.getId())
                    .status(v.getStatus())
                    .qrToken(v.getQrToken())
                    .build();
        }
    }
}
