package com.youngflix.Server.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoResponse {
    private String email;
    private String name;
    private String nickname;
    private String avatar;
}
