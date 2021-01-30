package com.waffle.demo.src.album;

import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface AlbumRepository extends CrudRepository<Album, Integer>{
    //@Query("SELECT a FROM Album a WHERE a.isDeleted='N'")
    List<Album> findByIsDeleted(String isDeleted);
    //데뷔앨범찾기
    Album findFirstBySingersAndIsDeletedOrderByReleaseDate(AlbumSinger albumSinger, String isDeleted);

    List<Album> findBySingers(Singer singer);
    //@Query("SELECT a FROM Album a WHERE a.albumIdx=?1")
    //lbum findById(int albumIdx);

    Album findByAlbumIdxAndAlbumLikesUsers(Integer albumIdx, User user);

    List<Album> findByGenresAndIsDeleted(Genre genre, String isDeleted);
}
