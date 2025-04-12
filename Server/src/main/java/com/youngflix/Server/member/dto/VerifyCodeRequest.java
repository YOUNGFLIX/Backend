package com.youngflix.Server.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequest {
    private String email;
    private String code;
    private String password;
    private String name;
    private String nickname;
}
