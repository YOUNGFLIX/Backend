package com.youngflix.Server.member.service;

import com.youngflix.Server.member.dto.SignupRequest;
import com.youngflix.Server.member.dto.MemberInfoResponse;

public interface MemberService {
    void signup(SignupRequest request);
    MemberInfoResponse getMyInfo(String email);
    MemberInfoResponse updateMyInfo(String email, MemberInfoResponse request);
    boolean isEmailDuplicate(String email);
}
