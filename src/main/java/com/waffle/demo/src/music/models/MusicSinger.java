package com.waffle.demo.src.music.models;

import com.waffle.demo.config.BaseEntity;
import javax.persistence.*;

import com.waffle.demo.src.singer.models.Singer;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "MusicSinger") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class MusicSinger extends BaseEntity implements Serializable {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "musicSingerIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer musicSingerIdx;

    //@Id // PK를 의미하는 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicIdx")
    private Music music;

    //@Id // PK를 의미하는 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singerIdx")
    private Singer singer;

    @Column(name = "type", nullable = false, length=20)
    private String type;

    @Column(name = "isSinger", nullable = false, length=1)
    private String isSinger;

    public MusicSinger(Music music, Singer singer, String type, String isSinger){
        this.music = music;
        this.singer = singer;
        this.type = type;
        this.isSinger = isSinger;
    }
}
