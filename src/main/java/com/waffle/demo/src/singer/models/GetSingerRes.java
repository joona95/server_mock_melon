package com.waffle.demo.src.singer.models;

import lombok.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetSingerRes {
    private final Integer singerIdx;
    private final String singerName;
    private final String nationality;
    private final String singerType;
    private final String gender;
    private final List<Integer> genresIdx;
    private final List<String> genres;
    private final String agency;
    private final String career;
    private final String singerIntroduction;
    private final String officialSite;
    private final String facebook;
    private final String twitter;
    private final String instagram;
    private final Integer debutMusicIdx;
    private final String debutMusicTitle;
    private final List<String> debutMusicSingersName;
    private final Date debutDate;
    private List<Integer> membersIdx;
    private List<String> members;
    private List<Integer> groupsIdx;
    private List<String> groupsName;
    private List<String> groupsGenres;
    private List<String> groupsLike;
    private List<Integer> agencySingersIdx;
    private List<String> agencySingersName;
    private List<String> agnecySingersGenres;
    private List<String> agencySingersLike;
}
