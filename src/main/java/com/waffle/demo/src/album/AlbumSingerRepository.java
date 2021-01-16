package com.waffle.demo.src.album;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.singer.models.Singer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface AlbumSingerRepository extends CrudRepository<AlbumSinger, Integer> {
    AlbumSinger findByAlbumAndSinger(Album album, Singer singer);

    List<AlbumSinger> findByTypeAndSingerAndIsDeleted(String type, Singer singer, String isDeleted);
}
