package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="member")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String profileImg;

    // 제3자인증 분류
    private String sso;

    // 일반, 멤버십, 관리자
    @Enumerated(EnumType.STRING)
    private Role role;

    // 가입일
    private LocalDateTime signupDate;

    // 탈퇴일
    private LocalDateTime signOutDate;

    // 계정 활성화 여부
    private boolean activate;

    @PrePersist
    protected void onCreate() {
        this.signupDate = LocalDateTime.now();
        this.activate = true;
        this.role = Role.ROLE_GENERAL;
    }
}
