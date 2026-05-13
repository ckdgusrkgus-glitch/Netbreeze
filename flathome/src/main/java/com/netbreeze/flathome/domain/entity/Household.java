package com.netbreeze.flathome.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 세대 엔티티
 * - 단지(Complex) 내 동·호수 단위
 */
@Entity
@Table(name = "households",
       uniqueConstraints = @UniqueConstraint(columnNames = {"complex_id", "building", "unit"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complex_id", nullable = false, length = 50)
    private String complexId;   // 단지 코드

    @Column(nullable = false, length = 10)
    private String building;    // 동 (예: "101")

    @Column(nullable = false, length = 10)
    private String unit;        // 호수 (예: "1203")

    @Column(name = "area_sqm")
    private Double areaSqm;     // 전용면적 ㎡

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<Resident> residents = new ArrayList<>();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<Visitor> visitors = new ArrayList<>();

    // ── 생성 팩토리 ──────────────────────────────
    public static Household of(String complexId, String building,
                                String unit, Double areaSqm) {
        Household h = new Household();
        h.complexId = complexId;
        h.building  = building;
        h.unit      = unit;
        h.areaSqm   = areaSqm;
        return h;
    }

    public String getFullAddress() {
        return building + "동 " + unit + "호";
    }
}
