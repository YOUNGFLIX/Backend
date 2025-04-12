package com.youngflix.Server.auth.service;

import com.youngflix.Server.auth.dto.LoginRequest;
import com.youngflix.Server.auth.dto.TokenResponse;
import com.youngflix.Server.auth.entity.RefreshToken;
import com.youngflix.Server.auth.repository.RefreshTokenRepository;
import com.youngflix.Server.auth.jwt.JwtUtil;
import com.youngflix.Server.member.entity.Member;
import com.youngflix.Server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;


    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!member.isEmailVerified()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateToken(member.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());
        refreshTokenRepository.findByEmail(member.getEmail())
                .ifPresentOrElse(
                    token -> {
                        token.setToken(refreshToken);
                        refreshTokenRepository.save(token);
                    },
                    () -> refreshTokenRepository.save(new RefreshToken(member.getEmail(), refreshToken))
                );
        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refreshToken 입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("저장된 refreshToken 이 없습니다."));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("refreshToken 이 일치하지 않습니다.");
        }

        String newAccessToken = jwtUtil.generateToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        refreshTokenRepository.findByEmail(email)
                .ifPresentOrElse(
                    token -> {
                        token.setToken(newRefreshToken);
                        refreshTokenRepository.save(token);
                    },
                    () -> refreshTokenRepository.save(new RefreshToken(email, newRefreshToken))
                );
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}
