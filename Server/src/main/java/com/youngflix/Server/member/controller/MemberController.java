package com.youngflix.Server.member.controller;

import com.youngflix.Server.common.response.ApiResponse;
import com.youngflix.Server.member.dto.MemberInfoResponse;
import com.youngflix.Server.member.dto.SignupRequest;
import com.youngflix.Server.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberServiceImpl memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        memberService.signup(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "회원가입 성공", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        MemberInfoResponse response = memberService.getMyInfo(user.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(200, "내 정보 조회 성공", response));
    }

}