package com.waffle.demo.src.music.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostMusicReq {
    @Positive
    private Integer albumIdx;
    @Size(min=1, max=45)
    private String musicTitle;
    @Size(max=1)
    private String isTitle;
    private List<@Positive Integer> singersIdx;
    private List<String> types;
    private List<String> isSingers;
    @Size(min=1, max=45)
    private String writing;
    @Size(min=1, max=45)
    private String composing;
    @Size(min=1, max=45)
    private String arranging;

    //@DateTimeFormat(pattern = "HH:mm:ss")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")
    private String musicLength;

    private String musicUrl;
    private String lyric;
    private List<Integer> genresIdx;
}
