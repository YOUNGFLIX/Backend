package com.youngflix.Server.member.controller;

import com.youngflix.Server.common.response.ApiResponse;
import com.youngflix.Server.member.dto.EmailRequest;
import com.youngflix.Server.member.dto.MemberInfoResponse;
import com.youngflix.Server.member.dto.SignupRequest;
import com.youngflix.Server.member.dto.VerifyCodeRequest;
import com.youngflix.Server.member.service.MemberService;
import com.youngflix.Server.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        memberService.signup(request);
        return ResponseEntity.status(201).body(new ApiResponse<>(201, "회원가입 성공", null));
    }

    /**
     * 내 정보 가져오기 (이메일, 이름, 닉네임, 프로필)
     */
    @GetMapping("/mypage/info")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        MemberInfoResponse response = memberService.getMyInfo(user.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(200, "내 정보 조회 성공", response));
    }

    /**
     * 내 정보 수정
     */
    @PutMapping("/mypage/info")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateCurrentUser(@AuthenticationPrincipal User user,
                                                                             @RequestBody MemberInfoResponse request) {
        MemberInfoResponse response = memberService.updateMyInfo(user.getUsername(), request);
        return ResponseEntity.ok(new ApiResponse<>(200, "내 정보 수정 성공", response));
    }
}