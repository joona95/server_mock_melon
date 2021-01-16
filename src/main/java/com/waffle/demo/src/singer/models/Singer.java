package com.waffle.demo.src.singer.models;

import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.genre.models.Genre;
//import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.music.models.Music;
//import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.user.models.User;
import lombok.*;

import javax.persistence.*;
import java.util.*;

import com.waffle.demo.config.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"albumSingers","musicSingers","memberLists","groupLists","singerLikesUsers","singerComments","genres"})
@Data // from lombok
@ToString(exclude={"albumSingers","musicSingers","memberLists","groupLists","singerLikesUsers","singerComments","genres"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Singer") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Singer extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "singerIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer singerIdx;

    @Column(name = "singerName", nullable = false, length=45)
    private String singerName;

    @Column(name = "channelName", nullable = false, length=100)
    private String channelName;

    //@OneToOne
    @Column(name = "profileMusicIdx")
    private int profileMusicIdx;

    @Column(name = "profileImgUrl")
    private String profileImgUrl;

    @Column(name = "nationality", length=45)
    private String nationality;

    @Column(name = "gender", nullable = false, length=1)
    private String gender;

    @Column(name = "singerType", nullable = false, length=10)
    private String singerType;

    @Column(name = "agency", nullable = false, length=45)
    private String agency;

    @Column(name = "career")
    private String career;

    @Column(name = "singerIntroduction")
    private String singerIntroduction;

    @Column(name = "officialSite")
    private String officialSite;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "instagram")
    private String instagram;

    //@ManyToMany(mappedBy = "singers", cascade = CascadeType.ALL)
    //private List<Album> albums = new ArrayList<Album>();
    @OneToMany(mappedBy = "singer", cascade = CascadeType.ALL)
    private List<AlbumSinger> albumSingers = new ArrayList<>();

    //@ManyToMany(mappedBy = "singers", cascade = CascadeType.ALL)
    //private List<Music> musics = new ArrayList<Music>();
    @OneToMany(mappedBy = "singer", cascade = CascadeType.ALL)
    private List<MusicSinger> musicSingers = new ArrayList<>();

    //@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="MemberList",
            joinColumns = @JoinColumn(name="singerIdx"),
            inverseJoinColumns = @JoinColumn(name = "memberIdx"))
    private List<Singer> memberLists = new ArrayList<>();

    @ManyToMany(mappedBy = "memberLists", cascade = CascadeType.ALL)
    private List<Singer> groupLists = new ArrayList<>();


    //@OneToMany(mappedBy = "singer", cascade = CascadeType.ALL)
    //private List<SingerLike> singerLikes = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="SingerLike",
            joinColumns = @JoinColumn(name="singerIdx"),
            inverseJoinColumns = @JoinColumn(name = "userIdx"))
    private List<User> singerLikesUsers = new ArrayList<User>();

    @OneToMany(mappedBy = "singer", cascade = CascadeType.ALL)
    private List<SingerComment> singerComments = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="SingerGenre",
            joinColumns = @JoinColumn(name="singerIdx"),
            inverseJoinColumns = @JoinColumn(name = "genreIdx"))
    private List<Genre> genres = new ArrayList<Genre>();




    public Singer(String singerName, String channelName, int profileMusicIdx, String profileImgUrl, String nationality, String gender, String singerType, String agency, String career, String singerIntroduction, String officialSite, String facebook, String twitter, String instagram){
        this.singerName = singerName;
        this.channelName = channelName;
        this.profileMusicIdx = profileMusicIdx;
        this.profileImgUrl = profileImgUrl;
        this.nationality = nationality;
        this.gender = gender;
        this.singerType = singerType;
        this.agency = agency;
        this.career = career;
        this.singerIntroduction = singerIntroduction;
        this.officialSite = officialSite;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
    }
}
