package com.waffle.demo.src.singer.models;

import lombok.*;

import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostSingerReCommentReq {
    @Size(min=1, max=200)
    String singerComment;
}
