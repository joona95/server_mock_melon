package com.waffle.demo.src.singer;

import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.user.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface SingerRepository extends CrudRepository<Singer, Integer> {
    //@Query("SELECT s From Singer s WHERE s.isDeleted='N'")
    List<Singer> findByIsDeleted(String isDeleted);

    List<Singer> findByAgencyAndIsDeleted(String agency, String isDeleted);

    Singer findBySingerIdxAndSingerLikesUsers(Integer singerIdx, User user);

    List<Singer> findByGenres(Genre genre);

    //List<Singer> findByMusics(Music music);

    //List<Singer> findByAlbums(Album album);
    //@Query("SELECT s FROM Singer s WHERE s.singerIdx=?1")
    //Singer findBySingerIdxAndIsDeleted(int singerIdx, String isDeleted);
}
