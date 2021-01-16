package com.waffle.demo.src.singer.models;

import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PatchSingerCommentReq {
    @Size (min=1, max=1000)
    String singerComment;
    @Positive
    Integer musicIdx;
    String photoUrl;
}
