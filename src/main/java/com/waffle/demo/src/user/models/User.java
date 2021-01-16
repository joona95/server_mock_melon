package com.waffle.demo.src.user.models;

import com.waffle.demo.src.album.models.Album;
//import com.waffle.demo.src.album.models.AlbumLike;
import com.waffle.demo.src.album.models.AlbumRate;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.singer.models.SingerComment;
import com.waffle.demo.src.singer.models.SingerCommentLike;
//import com.waffle.demo.src.singer.models.SingerLike;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.waffle.demo.config.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"userMusicPlay","userVouchers","currentPlaylistMusics", "singerLikes","singerComments","singerCommentLikes", "albumRates","musicLikes","albumLikes"})
@Data // from lombok
@ToString(exclude={"userMusicPlay","userVouchers","currentPlaylistMusics", "singerLikes","singerComments","singerCommentLikes", "albumRates","musicLikes","albumLikes"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "User") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class User extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "userIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userIdx;

    @Column(name = "userId", nullable = false, length = 20, unique = true)
    private String userId;

    @Column(name = "password", nullable = false, length = 20)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "phoneNum", nullable = false, length = 13)
    private String phoneNum;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "grade", nullable = false, length = 10)
    private String grade="normal";

    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    @Column(name = "authentication", nullable = false, length = 1)
    private String authentication = "N";

    @Column(name = "userProfileUrl")
    private String userProfileUrl;

    //@Column(name = "isDeleted", nullable = false, length=1)
    //private String isDeleted="N";

    //@OneToOne(mappedBy = "user", fetch=FetchType.LAZY)
    //private UserMusicPlay userMusicPlay;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserVoucher> userVouchers = new ArrayList<UserVoucher>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CurrentPlaylistMusic> currentPlaylistMusics = new ArrayList<>();

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    //private List<SingerLike> singerLikes = new ArrayList<>();
    @ManyToMany(mappedBy = "singerLikesUsers", cascade = CascadeType.ALL)
    private List<Singer> singerLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SingerComment> singerComments = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<SingerCommentLike> singerCommentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AlbumRate> albumRates = new ArrayList<>();

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    //private List<MusicLike> musicLikes = new ArrayList<>();
    @ManyToMany(mappedBy = "musicLikesUsers", cascade = CascadeType.ALL)
    private List<Music> musicLikes = new ArrayList<>();

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    //private List<AlbumLike> albumLikes = new ArrayList<>();
    @ManyToMany(mappedBy = "albumLikesUsers", cascade = CascadeType.ALL)
    private List<Album> albumLikes = new ArrayList<>();



    public User(String userId, String password, String nickname, String phoneNum, String email, String gender) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.phoneNum = phoneNum;
        this.email = email;
        this.gender = gender;
    }
}
