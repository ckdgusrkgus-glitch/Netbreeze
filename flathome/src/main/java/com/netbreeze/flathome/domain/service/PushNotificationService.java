package com.netbreeze.flathome.domain.service;

import com.netbreeze.flathome.domain.entity.Visitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 푸시 / 알림 발송 서비스
 * - FCM (Android)
 * - APNs (iOS)
 * - 카카오 알림톡 / SMS
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    // 실제 운영 시 Firebase Admin SDK, KakaoTalk API 등 주입
    // private final FirebaseMessaging firebaseMessaging;
    // private final KakaoNotificationClient kakaoClient;

    /**
     * 방문자 승인 완료 알림
     * - 방문자에게 SMS 또는 카카오 알림톡 발송
     */
    public void sendVisitorApprovalSms(Visitor visitor) {
        String message = String.format(
                "[플랫홈] %s님의 방문 예약이 승인되었습니다. 방문일: %s",
                visitor.getVisitorName(),
                visitor.getVisitDate()
        );
        log.info("[Push] 방문 승인 알림 발송 → visitorId={} name={} date={}",
                visitor.getId(), visitor.getVisitorName(), visitor.getVisitDate());

        // 실제 구현 예시 (카카오 알림톡)
        // kakaoClient.send(visitor.getPhone(), message);

        // 실제 구현 예시 (FCM)
        // Message fcmMsg = Message.builder()
        //     .setToken(visitor.getDeviceToken())
        //     .setNotification(Notification.builder()
        //         .setTitle("방문 예약 승인")
        //         .setBody(message)
        //         .build())
        //     .build();
        // firebaseMessaging.send(fcmMsg);
    }

    /**
     * 입주민에게 방문자 도착 알림
     * - 방문자가 QR 인증 완료 시 호출
     */
    public void sendVisitorArrivalPush(Visitor visitor) {
        log.info("[Push] 방문자 도착 알림 → householdId={} visitor={}",
                visitor.getHousehold().getId(), visitor.getVisitorName());

        // visitor.getHousehold().getResidents() 순회하며
        // 각 세대원의 deviceToken으로 FCM 발송
    }

    /**
     * 공지사항 등록 시 단지 전체 푸시
     */
    public void sendNoticePush(String complexId, String title) {
        log.info("[Push] 공지 푸시 발송 → complexId={} title={}", complexId, title);

        // Spring Batch Job으로 대량 발송 처리
        // 단지 내 전체 입주민 deviceToken 조회 후 FCM 멀티캐스트
    }
}
