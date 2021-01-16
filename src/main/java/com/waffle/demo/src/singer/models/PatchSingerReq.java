package com.waffle.demo.src.singer.models;

import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PatchSingerReq {
    @Size(min=1, max=45)
    private String singerName;
    @Size(min=1, max=100)
    private String channelName;
    @Digits(integer = 10, fraction = 0)
    private Integer profileMusicIdx;
    private String profileImgUrl;
    @Size(min=1, max=45)
    private String nationality;
    @Size(min=1, max=10)
    private String singerType;
    @Size(max=1)
    private String gender;
    private List<@Positive Integer> genresIdx;
    private String agency;
    private String career;
    private String singerIntroduction;
    private String officialSite;
    private String facebook;
    private String twitter;
    private String instagram;
    private List<@Positive Integer> membersIdx;
    private List<@Positive Integer> groupsIdx;
}
