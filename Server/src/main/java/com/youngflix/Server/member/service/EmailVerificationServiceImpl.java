package com.youngflix.Server.member.service;


import com.youngflix.Server.member.entity.VerificationToken;
import com.youngflix.Server.member.repository.MemberRepository;
import com.youngflix.Server.member.repository.VerificationTokenRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;

    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public String createEmailVerificationToken(String email) {
        // 기존 토큰 삭제
        verificationTokenRepository.deleteByEmail(email);

        String code = String.format("%06d", (int)(Math.random() * 1000000));
        VerificationToken verificationToken = VerificationToken.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .verified(false)
                .build();
        verificationTokenRepository.save(verificationToken);
        return code;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "[Youngflix] 이메일 인증 코드입니다.";

        String message = """
            <div style="font-family: 'Noto Sans', sans-serif; padding: 40px; background-color: #141414; color: #ffffff; border-radius: 10px; max-width: 500px; margin: auto;">
                <h1 style="color: #e50914; font-size: 24px; margin-bottom: 20px;">YOUNGFLIX 인증 코드</h1>
                <p style="font-size: 16px; margin-bottom: 10px;">아래 코드를 15분 이내에 입력해 주세요.</p>
                <div style="background-color: #333; padding: 20px; font-size: 28px; font-weight: bold; text-align: center; border-radius: 5px; letter-spacing: 2px;">
                    %s
                </div>
                <p style="font-size: 14px; color: #aaaaaa; margin-top: 30px;">
                    이 메일은 자동 발송되었으며 회신하지 마십시오. 문제가 있는 경우 gl021414@naver.com 으로 문의해주세요.
                </p>
                <hr style="border: none; border-top: 1px solid #444; margin: 30px 0;" />
                <p style="font-size: 13px; color: #888;">© 2025 Youngflix. All rights reserved.</p>
            </div>
        """.formatted(token);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(message, true); // true for HTML
            helper.setFrom(new InternetAddress("gl021414@naver.com", "Youngflix"));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    public boolean verifyCode(String email, String code) {
        VerificationToken vToken = verificationTokenRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));

        if (!vToken.getCode().equals(code)) {
            return false;
        }

        if (vToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        vToken.setVerified(true);
        verificationTokenRepository.save(vToken);
        return true;
    }

    public boolean sendVerificationCode(String email) {
        if (isEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String code = createEmailVerificationToken(email);
        sendVerificationEmail(email, code);
        return true;
    }

}
