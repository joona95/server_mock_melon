package com.waffle.demo.src.user.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostCurrentPlaylistReq {
    private List<@Positive Integer> musicsIdx;
    @PositiveOrZero
    private Integer createType;
}
