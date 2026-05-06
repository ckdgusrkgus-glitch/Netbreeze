package com.netbreeze.flathome.domain.repository;

import com.netbreeze.flathome.domain.entity.Notice;
import com.netbreeze.flathome.domain.entity.Notice.NoticeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 단지별 공지 페이징 조회
     * - 상단 고정(pinned=true) 먼저, 이후 최신순
     */
    @Query("""
           SELECT n FROM Notice n
           WHERE n.complexId = :complexId
           ORDER BY n.pinned DESC, n.createdAt DESC
           """)
    Page<Notice> findByComplexId(@Param("complexId") String complexId,
                                 Pageable pageable);

    /**
     * 카테고리 필터링
     */
    Page<Notice> findByComplexIdAndCategory(String complexId,
                                            NoticeCategory category,
                                            Pageable pageable);

    /**
     * 홈 화면용 최신 공지 N건 (긴급 + 일반 혼합)
     */
    @Query("""
           SELECT n FROM Notice n
           WHERE n.complexId = :complexId
           ORDER BY n.pinned DESC, n.createdAt DESC
           LIMIT :limit
           """)
    List<Notice> findRecentByComplexId(@Param("complexId") String complexId,
                                       @Param("limit") int limit);
}
