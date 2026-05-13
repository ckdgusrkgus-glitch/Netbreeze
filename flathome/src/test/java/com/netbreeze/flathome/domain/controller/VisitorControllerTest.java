package com.netbreeze.flathome.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbreeze.flathome.common.security.ResidentPrincipal;
import com.netbreeze.flathome.domain.dto.VisitorDto;
import com.netbreeze.flathome.domain.entity.Visitor.VisitPurpose;
import com.netbreeze.flathome.domain.entity.Visitor.VisitStatus;
import com.netbreeze.flathome.domain.service.VisitorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitorController.class)
@AutoConfigureMockMvc(addFilters = false)
class VisitorControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @MockBean  VisitorService visitorService;

    // Security 꺼져있으므로 principal은 null → householdId any()로 처리
    private UsernamePasswordAuthenticationToken mockAuth() {
        ResidentPrincipal p = ResidentPrincipal.builder()
                .residentId(1L).householdId(10L)
                .complexId("RAEMIAN_101").name("홍길동").role("RESIDENT")
                .build();
        return new UsernamePasswordAuthenticationToken(
                p, null, List.of(new SimpleGrantedAuthority("ROLE_RESIDENT")));
    }

    @Test
    @DisplayName("POST /visitors - 방문자 등록 성공")
    void register_success() throws Exception {
        var req = new java.util.HashMap<String, Object>();
        req.put("visitorName", "김방문");
        req.put("carPlate", "12가 3456");
        req.put("visitDate", LocalDate.now().toString());
        req.put("purpose", "FAMILY");

        VisitorDto.Summary summary = VisitorDto.Summary.builder()
                .id(1L).visitorName("김방문")
                .carPlate("12가 3456")
                .visitDate(LocalDate.now())
                .purpose(VisitPurpose.FAMILY)
                .status(VisitStatus.PENDING)
                .build();

        given(visitorService.registerVisitor(any(), any())).willReturn(summary);

        mockMvc.perform(post("/api/v1/visitors")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.visitorName").value("김방문"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /visitors - 이름 누락 시 400")
    void register_missingName_400() throws Exception {
        var req = new java.util.HashMap<String, Object>();
        req.put("visitDate", LocalDate.now().toString());
        req.put("purpose", "FAMILY");

        mockMvc.perform(post("/api/v1/visitors")
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("방문자 이름은 필수")));
    }

    @Test
    @DisplayName("GET /visitors/qr/verify - 유효한 QR 검증")
    void verifyQr_valid() throws Exception {
        given(visitorService.verifyQr("valid-token-abc")).willReturn(true);

        mockMvc.perform(get("/api/v1/visitors/qr/verify")
                        .param("token", "valid-token-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}