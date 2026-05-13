package com.netbreeze.flathome.domain.service;

import com.netbreeze.flathome.common.exception.NotFoundException;
import com.netbreeze.flathome.domain.dto.NoticeDto;
import com.netbreeze.flathome.domain.entity.Notice;
import com.netbreeze.flathome.domain.entity.Notice.NoticeCategory;
import com.netbreeze.flathome.domain.repository.NoticeRepository;
import com.netbreeze.flathome.domain.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock  NoticeRepository noticeRepository;
    @InjectMocks
    NoticeService noticeService;

    private static final String COMPLEX_ID = "RAEMIAN_101";

    @Test
    @DisplayName("공지 목록 조회 - 카테고리 없이 전체 조회")
    void getNotices_noCategory_returnsAll() {
        // given
        Notice n1 = Notice.create(COMPLEX_ID, NoticeCategory.URGENT,
                "엘리베이터 점검", "5월 7일 10시", "관리소", true);
        Notice n2 = Notice.create(COMPLEX_ID, NoticeCategory.GENERAL,
                "5월 관리비 안내", "내용", "관리소", false);

        var pageable = PageRequest.of(0, 20);
        given(noticeRepository.findByComplexId(COMPLEX_ID, pageable))
                .willReturn(new PageImpl<>(List.of(n1, n2)));

        // when
        var result = noticeService.getNotices(COMPLEX_ID, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("엘리베이터 점검");
    }

    @Test
    @DisplayName("공지 상세 조회 - 조회수 증가 확인")
    void getNotice_incrementsViewCount() {
        // given
        Notice notice = Notice.create(COMPLEX_ID, NoticeCategory.GENERAL,
                "제목", "내용", "관리소", false);
        given(noticeRepository.findById(1L)).willReturn(Optional.of(notice));

        int beforeCount = notice.getViewCount();

        // when
        NoticeDto.Detail detail = noticeService.getNotice(1L);

        // then
        assertThat(detail.getViewCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    @DisplayName("공지 상세 조회 - 존재하지 않으면 NotFoundException")
    void getNotice_notFound_throwsException() {
        given(noticeRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> noticeService.getNotice(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("공지 등록 - 저장 후 Detail 반환")
    void createNotice_savesAndReturns() {
        // given
        var req = new NoticeDto.CreateRequest();
        // Reflection으로 private 필드 세팅 (테스트 편의)
        setField(req, "category", NoticeCategory.URGENT);
        setField(req, "title", "긴급 공지");
        setField(req, "content", "공지 내용");
        setField(req, "pinned", true);

        given(noticeRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // when
        NoticeDto.Detail result =
                noticeService.createNotice(COMPLEX_ID, req, "관리소장");

        // then
        assertThat(result.getTitle()).isEqualTo("긴급 공지");
        assertThat(result.isPinned()).isTrue();
        then(noticeRepository).should().save(any(Notice.class));
    }

    // ── 테스트 헬퍼 ───────────────────────────────
    private void setField(Object obj, String fieldName, Object value) {
        try {
            var f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
