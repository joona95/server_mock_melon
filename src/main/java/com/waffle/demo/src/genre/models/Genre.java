package com.waffle.demo.src.genre.models;

import javax.persistence.*;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.singer.models.Singer;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude = {"singers","musics","albums"})
@Data // from lombok
@ToString(exclude = {"singers","musics","albums"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Genre") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Genre {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "genreIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer genreIdx;

    @Column(name = "genre", nullable = false, length=20)
    private String genre;

    @CreationTimestamp
    @Column(name = "createAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name="isDeleted", nullable = false, length=1)
    private String isDeleted = "N";

    @ManyToMany(mappedBy = "genres", cascade = CascadeType.ALL)
    private List<Singer> singers = new ArrayList<Singer>();

    @ManyToMany(mappedBy = "genres", cascade = CascadeType.ALL)
    private List<Music> musics = new ArrayList<Music>();

    @ManyToMany(mappedBy = "genres", cascade = CascadeType.ALL)
    private List<Album> albums = new ArrayList<Album>();


    public Genre(String genre){
        this.genre = genre;
    }
}
