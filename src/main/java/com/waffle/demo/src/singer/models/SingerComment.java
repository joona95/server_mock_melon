package com.waffle.demo.src.singer.models;


import com.waffle.demo.src.user.models.User;
import lombok.*;

import javax.persistence.*;
import java.util.*;

import com.waffle.demo.config.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude = {"singer", "user", "singerCommentLikes"})
@Data // from lombok
@ToString(exclude = {"singer", "user", "singerCommentLikes"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "SingerComment") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class SingerComment extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "singerCommentIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer singerCommentIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singerIdx", nullable = false)
    private Singer singer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "singerComment", nullable = false)
    private String singerComment;

    @Column(name = "singerCommentIdxParent")
    private Integer singerCommentIdxParent;

    @Column(name = "musicIdx")
    private Integer musicIdx;

    @Column(name = "photoUrl")
    private String photoUrl;

    @OneToMany(mappedBy = "singerComment", cascade = CascadeType.ALL)
    private List<SingerCommentLike> singerCommentLikes = new ArrayList<>();

    public SingerComment(Singer singer, User user, String singerComment){
        this.singer = singer;
        this.user = user;
        this.singerComment = singerComment;
    }

    public SingerComment(Singer singer, User user, String singerComment, Integer singerCommentIdxParent){
        this.singer = singer;
        this.user = user;
        this.singerComment = singerComment;
        this.singerCommentIdxParent = singerCommentIdxParent;
    }
}
