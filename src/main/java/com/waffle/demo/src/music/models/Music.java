package com.waffle.demo.src.music.models;

import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.CurrentPlayMusic;
import com.waffle.demo.src.user.models.CurrentPlaylistMusic;
import com.waffle.demo.src.user.models.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.util.*;

import com.waffle.demo.config.BaseEntity;
import com.waffle.demo.src.album.models.Album;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"album", "currentPlaylistMusics","singers","musicLikesUsers","chartRecord","genres"})
@Data // from lombok
@ToString(exclude={"album", "currentPlaylistMusics","singers","musicLikesUsers","chartRecord","genres"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Music") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Music extends BaseEntity{
    @Id // PK를 의미하는 어노테이션
    @Column(name = "musicIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer musicIdx;

    @ManyToOne
    @JoinColumn(name = "albumIdx", nullable = false)
    private Album album;

    @Column(name = "musicTitle", nullable = false, length=45)
    private String musicTitle;

    @Column(name = "isTitle", nullable = false, length=1)
    private String isTitle="N";

    @Column(name = "writing", nullable = false, length=45)
    private String writing;

    @Column(name = "composing", nullable = false, length=45)
    private String composing;

    @Column(name = "arranging", nullable = false, length=45)
    private String arranging;

    @Column(name = "musicLength", nullable = false)
    private Time musicLength;

    @Column(name = "musicUrl", nullable = false)
    private String musicUrl;

    @Column(name = "lyric", nullable = false)
    private String lyric;


    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    public List<CurrentPlaylistMusic> currentPlaylistMusics;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    private List<MusicSinger> singers = new ArrayList<>();
    /*
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="MusicSinger",
            joinColumns = @JoinColumn(name="musicIdx"),
            inverseJoinColumns = @JoinColumn(name = "singerIdx"))
    private List<Singer> singers = new ArrayList<Singer>();*/

    //@OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    //private List<MusicLike> musicLikes = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="MusicLike",
            joinColumns = @JoinColumn(name="musicIdx"),
            inverseJoinColumns = @JoinColumn(name = "userIdx"))
    private List<User> musicLikesUsers = new ArrayList<User>();

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    private List<Chart100> chartRecord = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="MusicGenre",
            joinColumns = @JoinColumn(name="musicIdx"),
            inverseJoinColumns = @JoinColumn(name = "genreIdx"))
    private List<Genre> genres = new ArrayList<Genre>();

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    private List<CurrentPlayMusic> currentPlayMusics = new ArrayList<CurrentPlayMusic>();


    public Music(Album album, String musicTitle, String isTitle, String writing, String composing, String arranging, Time musicLength, String musicUrl, String lyric){
        this.album = album;
        this.musicTitle = musicTitle;
        this.isTitle = isTitle;
        this.writing = writing;
        this.composing = composing;
        this.arranging = arranging;
        this.musicLength = musicLength;
        this.musicUrl = musicUrl;
        this.lyric = lyric;
    }

    public void setAlbum(Album album){
        if(this.album != null){
            this.album.getMusics().remove(this);
        }

        this.album = album;

        if(album != null){
            album.getMusics().add(this);
        }
    }

}
