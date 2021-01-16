package com.waffle.demo.src.user.models;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetCurrentPlaylistRes {
    private final Integer currentPlaylistMusicIdx;
    private final Integer musicIdx;
    private final String musicTitle;
    private final String musicUrl;
    private final List<Integer> musicSingersIdx;
    private final List<String> musicSingersName;
    private final Integer albumIdx;
    private final String albumImgUrl;
    private final String isShuffled;
    private final Integer replayType;
    private final Integer order;
    private final String isCurrentPlayMusic;
}
