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
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .verified(false)
                .build();
        verificationTokenRepository.save(verificationToken);
        return code;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "[Youngflix] 이메일 인증 코드입니다.";
        String message = "[Youngflix] 인증 코드: " + token + "\n\n해당 코드를 10분 이내에 입력해주세요.";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(message, false); // false means plain text
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
