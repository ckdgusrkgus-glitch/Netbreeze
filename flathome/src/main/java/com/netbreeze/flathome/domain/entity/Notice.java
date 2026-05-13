package com.netbreeze.flathome.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * 공지사항 엔티티
 */
@Entity
@Table(name = "notices",
       indexes = @Index(columnList = "complex_id, created_at DESC"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complex_id", nullable = false, length = 50)
    private String complexId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeCategory category;    // GENERAL, URGENT, FACILITY, BILLING

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "is_pinned")
    private boolean pinned = false;     // 상단 고정

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "author_name", length = 50)
    private String authorName;          // 작성자 (관리소장 등)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 팩토리 ──────────────────────────────
    public static Notice create(String complexId, NoticeCategory category,
                                String title, String content,
                                String authorName, boolean pinned) {
        Notice n = new Notice();
        n.complexId  = complexId;
        n.category   = category;
        n.title      = title;
        n.content    = content;
        n.authorName = authorName;
        n.pinned     = pinned;
        return n;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public enum NoticeCategory {
        GENERAL, URGENT, FACILITY, BILLING
    }
}
