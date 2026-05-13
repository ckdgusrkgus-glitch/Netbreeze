package com.netbreeze.flathome.common.security;

import lombok.Builder;
import lombok.Getter;

/**
 * JWT 인증 후 SecurityContext에 저장되는 입주민 정보
 */
@Getter
@Builder
public class ResidentPrincipal {
    private Long   residentId;
    private Long   householdId;
    private String complexId;
    private String name;
    private String role;   // RESIDENT, ADMIN
}
