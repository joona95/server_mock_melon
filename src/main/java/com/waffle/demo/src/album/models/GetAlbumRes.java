package com.waffle.demo.src.album.models;

import lombok.*;
import java.sql.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
public class GetAlbumRes {
    private final Integer albumIdx;
    private final String albumTitle;
    private final List<Integer> singersIdx;
    private final List<String> singersName;
    private final Date releaseDate;
    private final String albumImgUrl;
    private final String releaseCompany;
    private final String agency;
    private final String albumIntroduction;
    private final String albumType;
    private final List<Integer> genresIdx;
    private final List<String> genres;
    private final List<Integer> musicsIdx;
    private final List<String> musicsTitle;
    private final List<String> writings;
    private final List<String> composings;
    private final List<String> arrangings;
    private final List<String> lyrics;
}
