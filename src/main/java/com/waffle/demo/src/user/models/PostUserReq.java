package com.waffle.demo.src.user.models;

import lombok.*;

import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostUserReq {
    private String userId;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String phoneNum;
    private String email;
    @Size(max=1)
    private String gender;
}
