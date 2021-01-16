package com.waffle.demo.src.album.models;

import lombok.*;

import java.util.List;
import java.sql.Date;

@Getter
@AllArgsConstructor
public class GetAlbumChannelRes {
    private Integer albumIdx;
    private String albumTitle;
    private List<Integer> singersIdx;
    private List<String> singersName;
    private Date releaseDate;
    private String albumImgUrl;
    private String albumRate;
    private Integer albumRateUserCnt;
    private final String singerProfileUrl;
    private final String albumLike;
    private final Integer albumLikeCnt;
}
