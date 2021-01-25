package com.waffle.demo.src.music;

import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.singer.models.Singer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface MusicSingerRepository extends CrudRepository<MusicSinger, Integer> {
    MusicSinger findByMusicAndSinger(Music music, Singer singer);

    List<MusicSinger> findByMusicAndIsSinger(Music music, String isSinger);

    //최신순
    //발매
    @Query("SELECT ms from MusicSinger ms LEFT OUTER JOIN ms.music m ON m.isDeleted='N' LEFT OUTER JOIN ms.singer s ON s.isDeleted='N' WHERE ms.isSinger='Y' AND ms.singer=:singer AND ms.isDeleted='N' ORDER BY m.createdAt DESC")
    List<MusicSinger> findBySingerAndIsSingerAndIsDeletedOrderByMusicCreatedAt(@Param("singer") Singer singer);
    //type
    @Query("SELECT ms from MusicSinger ms LEFT OUTER JOIN ms.music m ON m.isDeleted='N' LEFT OUTER JOIN ms.singer s ON s.isDeleted='N' WHERE ms.type=:type AND ms.singer=:singer AND ms.isDeleted='N' ORDER BY m.createdAt DESC")
    List<MusicSinger> findByTypeAndSingerAndIsDeletedOrderByMusicCreatedAt(@Param("singer") Singer singer, @Param("type") String type);

    //인기순
    //발매
    @Query("SELECT ms from MusicSinger ms LEFT OUTER JOIN ms.music m ON m.isDeleted='N' LEFT OUTER JOIN ms.singer s ON s.isDeleted='N' WHERE ms.isSinger='Y' AND ms.singer=:singer AND ms.isDeleted='N' ORDER BY m.musicLikesUsers.size DESC")
    List<MusicSinger> findBySingerAndIsSingerAndIsDeletedOrderByMusicLikeCnt(@Param("singer") Singer singer);
    //type
    @Query("SELECT ms from MusicSinger ms LEFT OUTER JOIN ms.music m ON m.isDeleted='N' LEFT OUTER JOIN ms.singer s ON s.isDeleted='N' WHERE ms.type=:type AND ms.singer=:singer AND ms.isDeleted='N' ORDER BY m.musicLikesUsers.size DESC")
    List<MusicSinger> findByTypeAndSingerAndIsDeletedOrderByMusicLikeCnt(@Param("singer") Singer singer, @Param("type") String type);

    //가나다순
    //발매
    @Query("SELECT ms from MusicSinger ms LEFT OUTER JOIN ms.music m ON m.isDeleted='N' LEFT OUTER JOIN ms.singer s ON s.isDeleted='N' WHERE ms.isSinger='Y' AND ms.singer=:singer AND ms.isDeleted='N' ORDER BY m.musicTitle")
    List<MusicSinger> findBySingerAndIsSingerAndIsDeletedOrderByMusicTitle(@Param("singer") Singer singer);
    //type
    @Query("SELECT ms from MusicSinger ms LEFT OUTER JOIN ms.music m ON m.isDeleted='N' LEFT OUTER JOIN ms.singer s ON s.isDeleted='N' WHERE ms.type=:type AND ms.singer=:singer AND ms.isDeleted='N' ORDER BY m.musicTitle")
    List<MusicSinger> findByTypeAndSingerAndIsDeletedOrderByMusicTitle(@Param("singer") Singer singer, @Param("type") String type);
}
