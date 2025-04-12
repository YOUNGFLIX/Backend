package com.youngflix.Server.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "member")
@Getter
@Setter
@NoArgsConstructor // 기본생성자 생성
@AllArgsConstructor // 모든 필드 인자로 받는 생성자 생성
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    private String avatar;

    @Column(nullable = false)
    private boolean emailVerified = true;

}
