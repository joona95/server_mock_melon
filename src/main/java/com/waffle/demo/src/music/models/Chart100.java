package com.waffle.demo.src.music.models;

import com.waffle.demo.src.music.models.Music;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.waffle.demo.config.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude = {"music"})
@Data // from lombok
@ToString(exclude = {"music"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Chart100") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Chart100 {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "chartIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chartIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicIdx", nullable = false)
    private Music music;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @CreationTimestamp
    @Column(name = "createAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    public Chart100 (Music music, Integer rank){
        this.music = music;
        this.rank = rank;
    }
}
