package com.waffle.demo.src.music.models;

import lombok.*;
import java.sql.Time;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetMusicRes {
    private final Integer musicIdx;
    private final Integer albumIdx;
    private final String musicTitle;
    private final String isTitle;
    private final List<Integer> singersIdx;
    private final List<String> singersName;
    private final String writing;
    private final String composing;
    private final String arranging;
    private final String musicLength;
    private final String musicUrl;
    private final String lyric;
    private final List<Integer> genresIdx;
    private final List<String> genres;
}
