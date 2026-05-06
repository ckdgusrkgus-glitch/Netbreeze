package com.netbreeze.flathome.domain.controller;

import com.netbreeze.flathome.common.response.ApiResponse;
import com.netbreeze.flathome.common.security.CurrentResident;
import com.netbreeze.flathome.common.security.ResidentPrincipal;
import com.netbreeze.flathome.domain.dto.VisitorDto;
import com.netbreeze.flathome.domain.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 방문자 예약 API
 *
 * POST   /api/v1/visitors          방문자 등록
 * GET    /api/v1/visitors          방문자 목록
 * POST   /api/v1/visitors/{id}/approve   방문 승인 + QR 발급
 * POST   /api/v1/visitors/{id}/reject    방문 거절
 * GET    /api/v1/visitors/qr/verify      QR 검증 (출입 단말용)
 */
@RestController
@RequestMapping("/api/v1/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    // ── 방문자 등록 ────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VisitorDto.Summary> register(
            @CurrentResident ResidentPrincipal principal,
            @Valid @RequestBody VisitorDto.RegisterRequest req) {

        VisitorDto.Summary result =
                visitorService.registerVisitor(principal.getHouseholdId(), req);
        return ApiResponse.created(result);
    }

    // ── 방문자 목록 ────────────────────────────────
    @GetMapping
    public ApiResponse<Page<VisitorDto.Summary>> getVisitors(
            @CurrentResident ResidentPrincipal principal,
            @PageableDefault(size = 20, sort = "createdAt",
                             direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VisitorDto.Summary> result =
                visitorService.getVisitors(principal.getHouseholdId(), pageable);
        return ApiResponse.ok(result);
    }

    // ── 방문 승인 + QR 발급 ────────────────────────
    @PostMapping("/{visitorId}/approve")
    public ApiResponse<VisitorDto.ApproveResponse> approve(
            @PathVariable Long visitorId) {

        return ApiResponse.ok(visitorService.approveVisitor(visitorId));
    }

    // ── 방문 거절 ──────────────────────────────────
    @PostMapping("/{visitorId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable Long visitorId) {
        visitorService.rejectVisitor(visitorId);
    }

    // ── QR 검증 (출입 단말 → 서버) ────────────────
    @GetMapping("/qr/verify")
    public ApiResponse<Boolean> verifyQr(@RequestParam String token) {
        return ApiResponse.ok(visitorService.verifyQr(token));
    }
}
