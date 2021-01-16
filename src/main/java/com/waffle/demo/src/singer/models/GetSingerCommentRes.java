package com.waffle.demo.src.singer.models;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSingerCommentRes
{
    private final Integer singerCommentIdx;
    private final String singerComment;
    private final Integer musicIdx;
    private final String musicTitle;
    private final List<String> musicSingersName;
    private final String photoUrl;
    private final Integer userIdx;
    private final String isUser;
    private final String nickname;
    private final String profileImgUrl;
    private final String commentTime;
    private final String singerCommentLike;
    private final Integer singerCommentLikeCnt;
    private final String singerCommentHate;
    private final Integer singerCommentHateCnt;
}