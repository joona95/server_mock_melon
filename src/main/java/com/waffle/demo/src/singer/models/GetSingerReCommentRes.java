package com.waffle.demo.src.singer.models;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSingerReCommentRes {
    private final Integer singerCommentIdx;
    private final String singerComment;
    private final Integer userIdx;
    private final String isUser;
    private final String nickname;
    private final String commentTime;
}
