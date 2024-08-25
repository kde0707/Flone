package org.hermez.member.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class MemberLoginResponse {
    private int memberId;
    private int roleId;
    private String email;
    private String encodedPassword;
    private String name;
    private String phone;
}

