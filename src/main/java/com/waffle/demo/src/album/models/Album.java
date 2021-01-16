package com.waffle.demo.src.album.models;

import com.waffle.demo.config.BaseEntity;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.*;

import com.waffle.demo.src.music.models.Music;
import org.jetbrains.annotations.NotNull;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"musics","singers","albumRates","albumLikesUsers","genres"})
@Data // from lombok
@ToString(exclude={"musics","singers","albumRates","albumLikesUsers","genres"})
@Entity // 필수, Class 를 )Database Table화 해주는 것이다
@Table(name = "Album") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Album extends BaseEntity  {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "albumIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer albumIdx;

    @Column(name = "albumTitle", nullable = false, length=45)
    private String albumTitle;

    @Column(name = "releaseDate", nullable = false)
    private Date releaseDate;

    @Column(name = "albumImgUrl", nullable = false)
    private String albumImgUrl="";

    @Column(name = "releaseCompany", nullable = false, length=45)
    private String releaseCompany;

    @Column(name = "agency", nullable = false, length=45)
    private String agency;

    @Column(name = "albumIntroduction", nullable = false)
    private String albumIntroduction="";

    @Column(name = "albumType", nullable = false, length=20)
    private String albumType;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Music> musics = new ArrayList<Music>();

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<AlbumSinger> singers = new ArrayList<>();
    /*
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="AlbumSinger",
            joinColumns = @JoinColumn(name="albumIdx"),
            inverseJoinColumns = @JoinColumn(name = "singerIdx"))
    private List<Singer> singers = new ArrayList<Singer>();
*/


    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<AlbumRate> albumRates = new ArrayList<>();

    //@OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    //private List<AlbumLike> albumLikes = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="AlbumLike",
            joinColumns = @JoinColumn(name="albumIdx"),
            inverseJoinColumns = @JoinColumn(name = "userIdx"))
    private List<User> albumLikesUsers = new ArrayList<User>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="AlbumGenre",
            joinColumns = @JoinColumn(name="albumIdx"),
            inverseJoinColumns = @JoinColumn(name = "genreIdx"))
    private List<Genre> genres = new ArrayList<Genre>();


    public Album(String albumTitle, Date releaseDate, String albumImgUrl, String releaseCompany, String agency, String albumIntroduction, String albumType){
        this.albumTitle = albumTitle;
        this.releaseDate = releaseDate;
        this.albumImgUrl = albumImgUrl;
        this.releaseCompany = releaseCompany;
        this.agency = agency;
        this.albumIntroduction = albumIntroduction;
        this.albumType = albumType;

    }


}
