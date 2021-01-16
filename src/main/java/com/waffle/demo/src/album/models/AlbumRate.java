package com.waffle.demo.src.album.models;

import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude = {"album", "user"})
@Data // from lombok
@ToString(exclude = {"album", "user"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "AlbumRate") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class AlbumRate implements Serializable {
    @Id
    @Column(name = "albumRateIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer albumRateIdx;

    //@Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "albumIdx", nullable = false)
    private Album album;

    //@Id // PK를 의미하는 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "albumRate", nullable = false)
    private Float albumRate;

    @CreationTimestamp
    @Column(name = "createAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    public AlbumRate(Album album, User user, Float albumRate){
        this.album = album;
        this.user = user;
        this.albumRate = albumRate;
    }
}
