package com.waffle.demo.src.music.models;

import lombok.*;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetMusicsRes {
    private final Integer musicIdx;
    private final Integer albumIdx;
    private final String albumImgUrl;
    private final String musicTitle;
    private final String isTitle;
    private final List<Integer> singersIdx;
    private final List<String> singersName;
    private final List<Integer> genresIdx;
    private final List<String> genres;
}
