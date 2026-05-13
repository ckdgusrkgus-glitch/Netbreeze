package com.netbreeze.flathome.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 입주민 차량 엔티티
 */
@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "car_plate", nullable = false, unique = true, length = 20)
    private String carPlate;

    @Column(length = 50)
    private String model;           // 차종 (예: "현대 아반떼")

    @Column(name = "is_primary")
    private boolean primary = true; // 주차 추첨 대상 차량 여부

    public static Vehicle of(Resident resident, String carPlate,
                              String model, boolean primary) {
        Vehicle v = new Vehicle();
        v.resident = resident;
        v.carPlate = carPlate;
        v.model    = model;
        v.primary  = primary;
        return v;
    }
}
