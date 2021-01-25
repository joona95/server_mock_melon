package com.waffle.demo.src.album;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.singer.models.Singer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface AlbumSingerRepository extends CrudRepository<AlbumSinger, Integer> {
    AlbumSinger findByAlbumAndSinger(Album album, Singer singer);

    //최신순
    //전체
    @Query("SELECT als from AlbumSinger als LEFT OUTER JOIN als.album a ON a.isDeleted='N' LEFT OUTER JOIN als.singer s ON s.isDeleted='N' WHERE als.singer=:singer AND als.isDeleted='N' ORDER BY a.createdAt DESC")
    List<AlbumSinger> findBySingerAndIsDeletedOrderByAlbumCreatedAt(@Param("singer") Singer singer);
    //type
    @Query("SELECT als from AlbumSinger als LEFT OUTER JOIN als.album a ON a.isDeleted='N' LEFT OUTER JOIN als.singer s ON s.isDeleted='N' WHERE als.type=:type AND als.singer=:singer AND als.isDeleted='N' ORDER BY a.createdAt DESC")
    List<AlbumSinger> findByTypeAndSingerAndIsDeletedOrderByAlbumCreatedAt(@Param("singer") Singer singer, @Param("type") String type);

    //인기순
    //전체
    @Query("SELECT als from AlbumSinger als LEFT OUTER JOIN als.album a ON a.isDeleted='N' LEFT OUTER JOIN als.singer s ON s.isDeleted='N' WHERE als.singer=:singer AND als.isDeleted='N' ORDER BY a.albumLikesUsers.size DESC")
    List<AlbumSinger> findBySingerAndIsDeletedOrderByAlbumLikeCnt(@Param("singer") Singer singer);
    //type
    @Query("SELECT als from AlbumSinger als LEFT OUTER JOIN als.album a ON a.isDeleted='N' LEFT OUTER JOIN als.singer s ON s.isDeleted='N' WHERE als.type=:type AND als.singer=:singer AND als.isDeleted='N' ORDER BY a.albumLikesUsers.size DESC")
    List<AlbumSinger> findByTypeAndSingerAndIsDeletedOrderByAlbumLikeCnt(@Param("singer") Singer singer, @Param("type") String type);


    //가나다순
    //전체
    @Query("SELECT als from AlbumSinger als LEFT OUTER JOIN als.album a ON a.isDeleted='N' LEFT OUTER JOIN als.singer s ON s.isDeleted='N' WHERE als.singer=:singer AND als.isDeleted='N' ORDER BY a.albumTitle")
    List<AlbumSinger> findBySingerAndIsDeletedOrderByAlbumTitle(@Param("singer") Singer singer);
    //type
    @Query("SELECT als from AlbumSinger als LEFT OUTER JOIN als.album a ON a.isDeleted='N' LEFT OUTER JOIN als.singer s ON s.isDeleted='N' WHERE als.type=:type AND als.singer=:singer AND als.isDeleted='N' ORDER BY a.albumTitle")
    List<AlbumSinger> findByTypeAndSingerAndIsDeletedOrderByAlbumTitle(@Param("singer") Singer singer, @Param("type") String type);
}
