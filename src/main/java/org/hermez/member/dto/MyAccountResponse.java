package org.hermez.member.dto;

import lombok.Getter;

@Getter
public class MyAccountResponse {
    private int memberId;
    private int roleId;
    private String email;
    private String name;
    private String password;
    private String phone;
}