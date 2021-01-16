package com.waffle.demo.src.user.models;

import lombok.*;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import javax.persistence.*;

import com.waffle.demo.config.BaseEntity;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
//@ToString(exclude = {"user"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "UserMusicPlay") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class UserMusicPlay implements Serializable {
    @Id // PK를 의미하는 어노테이션
    //@OneToOne(fetch = FetchType.LAZY)
    @Column(name = "userIdx")
    private Integer userIdx;

    @Column(name = "startPosition")
    private Integer startPosition;

    @Column(name = "lastPosition")
    private Integer lastPosition;

    @Column(name = "currentPosition")
    private Integer currentPosition;

    @Column(name = "currentMusicStopPosition")
    private Time currentMusicStopPosition;

    @Column(name = "currentMusicGroup", nullable = false, length=10)
    private String currentMusicGroup="music";

    @Column(name = "isShuffled", nullable = false, length=1)
    private String isShuffled="N";

    @Column(name = "replayType", nullable = false)
    private Integer replayType=0;

    @UpdateTimestamp
    @Column(name = "updateAt", nullable = false)
    private Timestamp updatedAt;

    public UserMusicPlay (Integer userIdx, Integer startPosition, Integer lastPosition, Integer currentPosition){
        this.userIdx = userIdx;
        this.startPosition = startPosition;
        this.lastPosition = lastPosition;
        this.currentPosition = currentPosition;
    }
}
