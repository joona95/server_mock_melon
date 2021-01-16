package com.waffle.demo.src.singer.models;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSingersRes {
    private final Integer singerIdx;
    private final String singerName;
    private final String profileImgUrl;
    private final String nationality;
    private final String singerType;
    private final List<Integer> genresIdx;
    private final List<String> genres;
}
