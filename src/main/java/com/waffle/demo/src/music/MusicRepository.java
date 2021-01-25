package com.waffle.demo.src.music;

import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface MusicRepository extends CrudRepository<Music, Integer> {
    //@Query("SELECT m FROM Music m WHERE m.isDeleted='N'")
    List<Music> findByIsDeleted(String isDeleted);

    //@Query("SELECT m FROM Music m INNER JOIN Album ON m.album = ?1 WHERE m.isTitle='Y'")
    Music findFirstByAlbumAndIsTitleAndIsDeleted(Album album, String isTitle, String isDeleted);

    //@Query("SELECT m FROM Music m WHERE m.musicIdx=?1")
    //Music findById(int musicIdx);

    List<Music> findBySingers(Singer singer);

    //데뷔곡
    Music findByMusicIdxAndMusicLikesUsers(Integer musicIdx, User user);

    List<Music> findByGenres(Genre genre);

    @Query("SELECT m from Music m LEFT OUTER JOIN m.currentPlayMusics cpm ON cpm.createdAt > :yesterday GROUP BY m.musicIdx ORDER BY count(cpm) DESC")
    List<Music> findMusicsByCurrentPlayMusicCnt(@Param("yesterday") Timestamp yesterday, Pageable pageable);
}
