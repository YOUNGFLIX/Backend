package com.youngflix.Server.member.service;

public interface EmailVerificationService {
    boolean isEmailDuplicate(String email);
    String createEmailVerificationToken(String email);
    void sendVerificationEmail(String toEmail, String token);
    boolean verifyCode(String email, String code);
}
