package com.waffle.demo.src.album.models;

import lombok.*;
import java.sql.Date;
import java.util.List;


@Getter
@AllArgsConstructor
public class GetAlbumsRes {
    private final Integer albumIdx;
    private final String albumTitle;
    private final List<Integer> singersIdx;
    private final List<String> singersName;
    private final Date releaseDate;
    private final String albumImgUrl;
    private final String albumRate;
    private final Integer albumRateUserCnt;
    private final List<Integer> musicsIdx;
    private final List<Integer> genresIdx;
    private final List<String> genres;
}
