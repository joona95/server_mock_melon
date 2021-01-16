package com.waffle.demo.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserRes {
    private final Integer userIdx;
    private final String userId;
    private final String nickname;
    private final String phoneNum;
    private final String email;
    private final String grade;
    private final String gender;
    private final String authentication;
    private final String userProfileUrl;
}
