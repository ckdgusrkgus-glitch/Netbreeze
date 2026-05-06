package com.netbreeze.flathome.domain.service;

import com.netbreeze.flathome.common.exception.NotFoundException;
import com.netbreeze.flathome.domain.dto.NoticeDto;
import com.netbreeze.flathome.domain.entity.Notice;
import com.netbreeze.flathome.domain.entity.Notice.NoticeCategory;
import com.netbreeze.flathome.domain.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // ── 공지 목록 (페이징) ─────────────────────────
    @Transactional(readOnly = true)
    public Page<NoticeDto.Summary> getNotices(String complexId,
                                              NoticeCategory category,
                                              Pageable pageable) {
        Page<Notice> notices = (category != null)
                ? noticeRepository.findByComplexIdAndCategory(complexId, category, pageable)
                : noticeRepository.findByComplexId(complexId, pageable);

        return notices.map(NoticeDto.Summary::from);
    }

    // ── 홈 화면 최신 공지 N건 ─────────────────────
    @Transactional(readOnly = true)
    public List<NoticeDto.Summary> getRecentNotices(String complexId, int limit) {
        return noticeRepository.findRecentByComplexId(complexId, limit)
                               .stream()
                               .map(NoticeDto.Summary::from)
                               .toList();
    }

    // ── 공지 상세 + 조회수 증가 ───────────────────
    @Transactional
    public NoticeDto.Detail getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다. id=" + noticeId));

        notice.incrementViewCount();   // dirty checking으로 자동 UPDATE
        return NoticeDto.Detail.from(notice);
    }

    // ── 공지 등록 (관리자) ─────────────────────────
    @Transactional
    public NoticeDto.Detail createNotice(String complexId,
                                         NoticeDto.CreateRequest req,
                                         String authorName) {
        Notice notice = Notice.create(
                complexId,
                req.getCategory(),
                req.getTitle(),
                req.getContent(),
                authorName,
                req.isPinned()
        );
        noticeRepository.save(notice);
        log.info("[Notice] 공지 등록 complexId={} category={} title={}",
                 complexId, req.getCategory(), req.getTitle());
        return NoticeDto.Detail.from(notice);
    }
}
