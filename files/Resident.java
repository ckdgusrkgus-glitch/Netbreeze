package com.netbreeze.flathome.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 입주민 엔티티
 * - 한 세대(Household)에 여러 세대원이 속할 수 있음
 */
@Entity
@Table(name = "residents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"household", "vehicles"})
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 세대 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResidentRole role;   // OWNER, TENANT, FAMILY

    @Column(name = "device_token", length = 255)
    private String deviceToken;  // FCM 푸시 토큰

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── 생성 팩토리 ──────────────────────────────
    public static Resident create(Household household, String name,
                                  String email, String phone, ResidentRole role) {
        Resident r = new Resident();
        r.household = household;
        r.name = name;
        r.email = email;
        r.phone = phone;
        r.role = role;
        return r;
    }

    // ── 도메인 메서드 ─────────────────────────────
    public void updateDeviceToken(String token) {
        this.deviceToken = token;
    }

    public void updateContact(String phone) {
        this.phone = phone;
    }

    public enum ResidentRole {
        OWNER, TENANT, FAMILY
    }
}
