package com.youngflix.Server.member.service;

import com.youngflix.Server.member.dto.SignupRequest;
import com.youngflix.Server.member.dto.MemberInfoResponse;
import com.youngflix.Server.member.entity.Member;
import com.youngflix.Server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(SignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member member = new Member(null, request.getEmail(), encodedPassword, request.getName(), request.getNickname(), null);
        memberRepository.save(member);
    }

    @Override
    public MemberInfoResponse getMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return new MemberInfoResponse(
            member.getEmail(),
            member.getName(),
            member.getNickname(),
            member.getAvatar()
        );
    }

    @Override
    public MemberInfoResponse updateMyInfo(String email, MemberInfoResponse request) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setAvatar(request.getAvatar());

        memberRepository.save(member);

        return new MemberInfoResponse(
            member.getEmail(),
            member.getName(),
            member.getNickname(),
            member.getAvatar()
        );
    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
