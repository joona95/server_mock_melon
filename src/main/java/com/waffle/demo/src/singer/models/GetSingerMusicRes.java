package com.waffle.demo.src.singer.models;

import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetSingerMusicRes {
    private final Integer musicIdx;
    private final String musicTitle;
    private final Integer albumIdx;
    private final String albumImgUrl;
    private final List<Integer> musicSingerIdx;
    private final List<String> musicSingerName;
    private final String isTitle;
    private final String musicSingerType;
}
