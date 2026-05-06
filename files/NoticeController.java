package com.netbreeze.flathome.domain.controller;

import com.netbreeze.flathome.common.response.ApiResponse;
import com.netbreeze.flathome.common.security.CurrentResident;
import com.netbreeze.flathome.common.security.ResidentPrincipal;
import com.netbreeze.flathome.domain.dto.NoticeDto;
import com.netbreeze.flathome.domain.entity.Notice.NoticeCategory;
import com.netbreeze.flathome.domain.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공지사항 API
 *
 * GET  /api/v1/notices          공지 목록 (페이징)
 * GET  /api/v1/notices/recent   홈 화면 최신 공지
 * GET  /api/v1/notices/{id}     공지 상세
 * POST /api/v1/notices          공지 등록 (관리자 전용)
 */
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // ── 공지 목록 ──────────────────────────────────
    @GetMapping
    public ApiResponse<Page<NoticeDto.Summary>> getNotices(
            @CurrentResident ResidentPrincipal principal,
            @RequestParam(required = false) NoticeCategory category,
            @PageableDefault(size = 20, sort = "createdAt",
                             direction = Sort.Direction.DESC) Pageable pageable) {

        Page<NoticeDto.Summary> result =
                noticeService.getNotices(principal.getComplexId(), category, pageable);
        return ApiResponse.ok(result);
    }

    // ── 홈 화면 최신 공지 5건 ─────────────────────
    @GetMapping("/recent")
    public ApiResponse<List<NoticeDto.Summary>> getRecentNotices(
            @CurrentResident ResidentPrincipal principal,
            @RequestParam(defaultValue = "5") int limit) {

        List<NoticeDto.Summary> result =
                noticeService.getRecentNotices(principal.getComplexId(), limit);
        return ApiResponse.ok(result);
    }

    // ── 공지 상세 ──────────────────────────────────
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDto.Detail> getNotice(@PathVariable Long noticeId) {
        return ApiResponse.ok(noticeService.getNotice(noticeId));
    }

    // ── 공지 등록 (관리자 전용) ────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NoticeDto.Detail> createNotice(
            @CurrentResident ResidentPrincipal principal,
            @Valid @RequestBody NoticeDto.CreateRequest req) {

        NoticeDto.Detail created =
                noticeService.createNotice(principal.getComplexId(), req, principal.getName());
        return ApiResponse.created(created);
    }
}
