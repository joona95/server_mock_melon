package com.waffle.demo.src.user.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PatchUserReq {
    private String password;
    private String nickname;
    private String phoneNum;
    private String email;
    @Size(max=1)
    private String authentication;
    private String userProfileUrl;
}
