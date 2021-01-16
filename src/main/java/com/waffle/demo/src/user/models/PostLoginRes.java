package com.waffle.demo.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLoginRes {
    private final Integer userIdx;
    private final String userId;
    private final String nickname;
    private final String jwt;
}
