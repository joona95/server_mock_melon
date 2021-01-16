package com.waffle.demo.src.singer.models;

import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetSingerAlbumRes {
    private final Integer albumIdx;
    private final String albumTitle;
    private final String albumImgUrl;
    private final String albumSingerType;
    private final List<String> albumSingerName;
    private final Integer musicCnt;
    private final List<Integer> musicsIdx;
}
