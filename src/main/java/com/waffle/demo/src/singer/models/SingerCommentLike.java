package com.waffle.demo.src.singer.models;

import com.waffle.demo.src.user.models.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import com.waffle.demo.config.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"singerComment", "user"})
@Data // from lombok
@ToString(exclude={"singerComment", "user"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "SingerCommentLike") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class SingerCommentLike implements Serializable {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "singerCommentLikeIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer singerCommentLikeIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singerCommentIdx", nullable = false)
    private SingerComment singerComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "likeOrHate", nullable = false, length = 1)
    private String like;

    @CreationTimestamp
    @Column(name = "createAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name="isDeleted", nullable = false, length=1)
    private String isDeleted = "N";

    public SingerCommentLike(SingerComment singerComment, User user, String like){
        this.singerComment = singerComment;
        this.user = user;
        this.like = like;
    }

}
