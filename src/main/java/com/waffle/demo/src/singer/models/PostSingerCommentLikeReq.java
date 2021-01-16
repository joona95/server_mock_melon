package com.waffle.demo.src.singer.models;

import lombok.*;

import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostSingerCommentLikeReq {
    @Size(max=1)
    private String like;
}
