package com.waffle.demo.src.user;

import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.user.models.CurrentPlayMusic;
import com.waffle.demo.src.user.models.CurrentPlaylistMusic;
import com.waffle.demo.src.user.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface CurrentPlayMusicRepository extends CrudRepository<CurrentPlayMusic, Integer> {
    CurrentPlayMusic findFirstByUserAndIsDeletedOrderByCreatedAtDesc(User user, String isDeleted);

    List<CurrentPlayMusic> findByUserAndMusicAndIsDeletedOrderByCreatedAt(User user, Music music, String isDeleted);

    @Query("SELECT cpm from CurrentPlayMusic cpm WHERE cpm.createdAt > :yesterday AND cpm.music=:music")
    List<CurrentPlayMusic> findByYesterdayUserPlayMusic(@Param("yesterday") Timestamp yesterday, @Param("music") Music music);
}
