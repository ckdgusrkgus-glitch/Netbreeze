package com.netbreeze.flathome.domain.dto;

import com.netbreeze.flathome.domain.entity.Notice;
import com.netbreeze.flathome.domain.entity.Notice.NoticeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

public class NoticeDto {

    // ── 요청 DTO ──────────────────────────────────
    @Getter
    public static class CreateRequest {

        @NotNull(message = "카테고리는 필수입니다.")
        private NoticeCategory category;

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자 이내여야 합니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        private boolean pinned = false;
    }

    // ── 응답 DTO (목록) ────────────────────────────
    @Getter
    @Builder
    public static class Summary {
        private Long id;
        private NoticeCategory category;
        private String title;
        private String authorName;
        private int viewCount;
        private boolean pinned;
        private LocalDateTime createdAt;

        public static Summary from(Notice notice) {
            return Summary.builder()
                    .id(notice.getId())
                    .category(notice.getCategory())
                    .title(notice.getTitle())
                    .authorName(notice.getAuthorName())
                    .viewCount(notice.getViewCount())
                    .pinned(notice.isPinned())
                    .createdAt(notice.getCreatedAt())
                    .build();
        }
    }

    // ── 응답 DTO (상세) ────────────────────────────
    @Getter
    @Builder
    public static class Detail {
        private Long id;
        private NoticeCategory category;
        private String title;
        private String content;
        private String authorName;
        private int viewCount;
        private boolean pinned;
        private LocalDateTime createdAt;

        public static Detail from(Notice notice) {
            return Detail.builder()
                    .id(notice.getId())
                    .category(notice.getCategory())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .authorName(notice.getAuthorName())
                    .viewCount(notice.getViewCount())
                    .pinned(notice.isPinned())
                    .createdAt(notice.getCreatedAt())
                    .build();
        }
    }
}
