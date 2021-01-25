package com.waffle.demo.src.music;

import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.music.models.Chart100;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface Chart100Repository extends CrudRepository<Chart100, Integer> {
    List<Chart100> findByIsDeleted(String isDeleted);
    Chart100 findByMusicAndIsDeleted(Music music, String isDeleted);
    Chart100 findFirstByMusicOrderByRankDesc(Music music);
    List<Chart100> findByMusicAndRankOrderByCreatedAt(Music music, Integer rank);
}
