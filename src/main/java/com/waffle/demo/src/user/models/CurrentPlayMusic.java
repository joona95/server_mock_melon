package com.waffle.demo.src.user.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.waffle.demo.config.BaseEntity;
import com.waffle.demo.src.music.models.Music;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"user", "music"})
@Data // from lombok
@ToString(exclude={"user", "music"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "CurrentPlayMusic") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class CurrentPlayMusic {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "currentPlayMusicIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer currentPlayMusicIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicIdx")
    private Music music;

    @CreationTimestamp
    @Column(name = "createAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name="isDeleted", nullable = false, length=1)
    private String isDeleted = "N";

    public CurrentPlayMusic(User user, Music music){
        this.user = user;
        this.music = music;
    }
}
