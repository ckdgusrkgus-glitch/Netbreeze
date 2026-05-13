package com.netbreeze.flathome.common.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.lang.annotation.*;

/**
 * 컨트롤러 파라미터에 @CurrentResident를 붙이면
 * SecurityContext의 ResidentPrincipal을 자동 주입
 *
 * 사용 예:
 *   public ApiResponse<...> myMethod(@CurrentResident ResidentPrincipal p) { ... }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentResident {
}
