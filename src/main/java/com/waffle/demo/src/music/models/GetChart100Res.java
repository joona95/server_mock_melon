package com.waffle.demo.src.music.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetChart100Res {
    private final Integer musicIdx;
    private final String musicTitle;
    private final String musicUrl;
    private final Integer albumIdx;
    private final String albumImgUrl;
    private final List<Integer> singersIdx;
    private final List<String> singersName;
    private final Integer rank;
}
