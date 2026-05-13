package com.netbreeze.flathome.domain.repository;

import com.netbreeze.flathome.domain.entity.Visitor;
import com.netbreeze.flathome.domain.entity.Visitor.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Page<Visitor> findByHouseholdIdOrderByCreatedAtDesc(Long householdId,
                                                         Pageable pageable);

    Optional<Visitor> findByQrToken(String qrToken);

    /**
     * 오늘 방문 예정 차량번호 조회 (주차 차단기 연동용)
     */
    @Query("""
           SELECT v FROM Visitor v
           WHERE v.household.complexId = :complexId
             AND v.visitDate = :today
             AND v.status = 'APPROVED'
             AND v.carPlate IS NOT NULL
           """)
    java.util.List<Visitor> findApprovedVisitorsToday(
            @Param("complexId") String complexId,
            @Param("today") LocalDate today);

    /**
     * 만료 처리 배치 - 방문일이 지난 PENDING/APPROVED 방문자 일괄 EXPIRED 처리
     */
    @Modifying
    @Query("""
           UPDATE Visitor v
           SET v.status = 'EXPIRED'
           WHERE v.visitDate < :today
             AND v.status IN ('PENDING', 'APPROVED')
           """)
    int expireOldVisitors(@Param("today") LocalDate today);
}
