package com.waffle.demo.src.music;

import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.singer.models.Singer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface MusicSingerRepository extends CrudRepository<MusicSinger, Integer> {
    MusicSinger findByMusicAndSinger(Music music, Singer singer);

    List<MusicSinger> findByMusicAndIsSinger(Music music, String isSinger);

    List<MusicSinger> findByTypeAndSingerAndIsDeleted(String type, Singer singer, String isDelted);
}
