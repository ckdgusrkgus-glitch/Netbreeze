package com.netbreeze.flathome.domain.service;

import com.netbreeze.flathome.common.exception.NotFoundException;
import com.netbreeze.flathome.domain.dto.VisitorDto;
import com.netbreeze.flathome.domain.entity.Household;
import com.netbreeze.flathome.domain.entity.Visitor;
import com.netbreeze.flathome.domain.repository.HouseholdRepository;
import com.netbreeze.flathome.domain.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository    visitorRepository;
    private final HouseholdRepository  householdRepository;
    private final PushNotificationService pushService;

    // ── 방문자 등록 ────────────────────────────────
    @Transactional
    public VisitorDto.Summary registerVisitor(Long householdId,
                                              VisitorDto.RegisterRequest req) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new NotFoundException("세대를 찾을 수 없습니다."));

        Visitor visitor = Visitor.create(
                household,
                req.getVisitorName(),
                req.getCarPlate(),
                req.getVisitDate(),
                req.getPurpose()
        );
        visitorRepository.save(visitor);
        log.info("[Visitor] 방문 등록 household={} visitor={} date={}",
                 householdId, req.getVisitorName(), req.getVisitDate());
        return VisitorDto.Summary.from(visitor);
    }

    // ── 방문자 목록 조회 ───────────────────────────
    @Transactional(readOnly = true)
    public Page<VisitorDto.Summary> getVisitors(Long householdId, Pageable pageable) {
        return visitorRepository
                .findByHouseholdIdOrderByCreatedAtDesc(householdId, pageable)
                .map(VisitorDto.Summary::from);
    }

    // ── 방문자 승인 + QR 발급 ──────────────────────
    @Transactional
    public VisitorDto.ApproveResponse approveVisitor(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new NotFoundException("방문 예약을 찾을 수 없습니다."));

        String qrToken = generateQrToken(visitor);
        visitor.approve(qrToken);

        // 방문자에게 카카오 알림톡 / SMS 발송
        pushService.sendVisitorApprovalSms(visitor);

        return VisitorDto.ApproveResponse.from(visitor);
    }

    // ── QR 검증 (출입 단말에서 호출) ──────────────
    @Transactional(readOnly = true)
    public boolean verifyQr(String qrToken) {
        return visitorRepository.findByQrToken(qrToken)
                .map(v -> v.getStatus() == Visitor.VisitStatus.APPROVED
                       && !v.getVisitDate().isBefore(LocalDate.now()))
                .orElse(false);
    }

    // ── 오늘 방문 승인 차량 목록 (주차 연동용) ────
    @Transactional(readOnly = true)
    public List<String> getApprovedCarPlates(String complexId) {
        return visitorRepository
                .findApprovedVisitorsToday(complexId, LocalDate.now())
                .stream()
                .map(Visitor::getCarPlate)
                .toList();
    }

    // ── 만료 배치 (매일 새벽 1시) ─────────────────
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expireOldVisitors() {
        int count = visitorRepository.expireOldVisitors(LocalDate.now());
        log.info("[Visitor] 만료 처리 {}건", count);
    }

    // ── 내부 유틸 ──────────────────────────────────
    private String generateQrToken(Visitor visitor) {
        // 실무: HMAC-SHA256(visitorId + visitDate + secretKey)
        String raw = visitor.getId() + ":" + visitor.getVisitDate() + ":" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
    }
}
