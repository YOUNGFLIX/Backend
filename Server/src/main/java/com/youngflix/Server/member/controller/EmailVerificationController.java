package com.youngflix.Server.member.controller;


import com.youngflix.Server.common.response.ApiResponse;
import com.youngflix.Server.member.dto.EmailRequest;
import com.youngflix.Server.member.dto.VerifyCodeRequest;
import com.youngflix.Server.member.service.EmailVerificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
public class EmailVerificationController {

    private EmailVerificationServiceImpl emailVerificationService;

    /**
     * 중복 이메일 체크
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = emailVerificationService.isEmailDuplicate(email);
        return ResponseEntity.ok(new ApiResponse<>( 200, "이메일 중복 검사 결과", isDuplicate));
    }

    /**
     * 이메일을 입력 받아 인증 메일 전송
     */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Boolean>> sendCode(@RequestBody EmailRequest emailRequest) {
        boolean success = emailVerificationService.sendVerificationCode(emailRequest.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(200, success ? "인증 코드 전송 성공" : "인증 코드 전송 실패", success));
    }

    /**
     * 이메일 인증 코드 판별
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean success = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new ApiResponse<>(200, success ? "인증 성공" : "인증 실패", success));
    }
}
