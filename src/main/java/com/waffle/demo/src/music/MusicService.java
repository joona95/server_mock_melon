package com.waffle.demo.src.music;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.utils.JwtService;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.genre.GenreProvider;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.music.models.*;
import com.waffle.demo.src.album.AlbumProvider;
import com.waffle.demo.src.album.AlbumRepository;
import com.waffle.demo.src.singer.SingerProvider;
import com.waffle.demo.src.singer.SingerRepository;
//import com.waffle.demo.src.singer.models.SingerLike;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.models.User;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class MusicService {
    private final MusicRepository musicRepository;
    private final AlbumRepository albumRepository;
    private final SingerRepository singerRepository;
    private final MusicSingerRepository musicSingerRepository;
    private final UserProvider userProvider;
    private final MusicProvider musicProvider;
    private final AlbumProvider albumProvider;
    private final SingerProvider singerProvider;
    private final GenreProvider genreProvider;
    private final JwtService jwtService;

    @Autowired
    public MusicService(MusicRepository musicRepository, AlbumRepository albumRepository, MusicSingerRepository musicSingerRepository, SingerRepository singerRepository, UserProvider userProvider, MusicProvider musicProvider, AlbumProvider albumProvider, SingerProvider singerProvider, GenreProvider genreProvider, JwtService jwtService){
        this.musicRepository = musicRepository;
        this.albumRepository = albumRepository;
        this.musicSingerRepository = musicSingerRepository;
        this.singerRepository = singerRepository;
        this.userProvider = userProvider;
        this.musicProvider = musicProvider;
        this.albumProvider = albumProvider;
        this.singerProvider = singerProvider;
        this.genreProvider = genreProvider;
        this.jwtService = jwtService;
    }

    /**
     * 음악 등록
     * @param postMusicReq
     * @return PostMusicRes
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public PostMusicRes createMusic(PostMusicReq postMusicReq) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        //음악 정보 생성
        Integer albumIdx = postMusicReq.getAlbumIdx();
        String musicTitle = postMusicReq.getMusicTitle();
        String isTitle = postMusicReq.getIsTitle();
        String writing = postMusicReq.getWriting();
        String composing = postMusicReq.getComposing();
        String arranging = postMusicReq.getArranging();
        Time musicLength = Time.valueOf(postMusicReq.getMusicLength());
        String musicUrl = postMusicReq.getMusicUrl();
        String lyric = postMusicReq.getLyric();

        Album album = albumProvider.retrieveAlbumByAlbumIdx(albumIdx);
        String albumTitle = album.getAlbumTitle();

        Music music = new Music(album, musicTitle, isTitle, writing, composing, arranging, musicLength, musicUrl, lyric);

        List<Integer> genresIdx = postMusicReq.getGenresIdx();
        for(int i=0;i<genresIdx.size();i++){
            Genre genre = genreProvider.retrieveGenreByGenreIdx(genresIdx.get(i));
            music.getGenres().add(genre);
            genre.getMusics().add(music);
        }

        //음악 정보 저장
        try{
            music = musicRepository.save(music);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_MUSIC);
        }

        List<Integer> singersIdx = postMusicReq.getSingersIdx();
        List<String> types = postMusicReq.getTypes();
        List<String> isSingers = postMusicReq.getIsSingers();

        for(int i=0;i<singersIdx.size();i++){
            Singer singer = singerProvider.retrieveSingerBySingerIdx(singersIdx.get(i));
            MusicSinger musicSinger = new MusicSinger(music, singer, types.get(i), isSingers.get(i));
            try{
                musicSinger = musicSingerRepository.save(musicSinger);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_POST_MUSIC);
            }
            music.getSingers().add(musicSinger);
            singer.getMusicSingers().add(musicSinger);
        }

        Integer musicIdx = music.getMusicIdx();

        return new PostMusicRes(musicIdx);

    }

    /**
     * 음악 정보 수정 (POST uri 가 겹쳤을때의 예시 용도)
     * @param patchMusicReq
     * @return PatchMusicRes
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public PatchMusicRes updateMusic(@NonNull Integer musicIdx, PatchMusicReq patchMusicReq) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Music music = musicProvider.retrieveMusicByMusicIdx(musicIdx);

        //음악 정보 수정
        music.setMusicTitle(patchMusicReq.getMusicTitle());
        music.setIsTitle(patchMusicReq.getIsTitle());
        music.setWriting(patchMusicReq.getWriting());
        music.setComposing(patchMusicReq.getComposing());
        music.setArranging(patchMusicReq.getArranging());
        music.setMusicLength(Time.valueOf(patchMusicReq.getMusicLength()));
        music.setMusicUrl(patchMusicReq.getMusicUrl());
        music.setLyric(patchMusicReq.getLyric());

        List<Integer> genresIdx = patchMusicReq.getGenresIdx();
        music.getGenres().clear();
        for(int i=0;i<genresIdx.size();i++){
            Genre genre = genreProvider.retrieveGenreByGenreIdx(genresIdx.get(i));
            genre.getMusics().remove(music);
            music.getGenres().add(genre);
            genre.getMusics().add(music);
        }

        try{
            music = musicRepository.save(music);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_MUSIC);
        }

        for(int i=0;i<music.getSingers().size();i++){
            MusicSinger musicSinger = music.getSingers().get(i);
            musicSinger.setIsDeleted("Y");
            try{
                musicSinger = musicSingerRepository.save(musicSinger);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_PATCH_MUSIC);
            }
        }

        List<Integer> singersIdx = patchMusicReq.getSingersIdx();
        List<String> types = patchMusicReq.getTypes();
        List<String> isSingers = patchMusicReq.getIsSingers();

        for(int i=0;i<singersIdx.size();i++){
            Singer singer = singerProvider.retrieveSingerBySingerIdx(singersIdx.get(i));
            MusicSinger musicSinger= musicSingerRepository.findByMusicAndSinger(music, singer);
            if(musicSinger== null) {
                musicSinger = new MusicSinger(music, singer, types.get(i), isSingers.get(i));
            }
            else{
                musicSinger.setIsDeleted("N");
            }

            try{
                musicSingerRepository.save(musicSinger);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_PATCH_MUSIC);
            }
        }



        return new PatchMusicRes(musicIdx);

    }

    /**
     * 음악 삭제
     * @param musicIdx
     * @throws BaseException
     */
    public void deleteMusic(Integer musicIdx) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Music music = musicProvider.retrieveMusicByMusicIdx(musicIdx);

        music.setIsDeleted("Y");
        try{
            musicRepository.save(music);
        } catch(Exception ignored){
            throw new BaseException(FAILED_TO_DELETE_MUSIC);
        }
    }

    /**
     * 음악 좋아요 생성/취소
     * @param musicIdx
     * @throws BaseException
     * @return boolean
     */
    public boolean createOrDeleteMusicLike(Integer musicIdx) throws BaseException {

        boolean createMusicLike = true;

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());
        Music music = musicProvider.retrieveMusicByMusicIdx(musicIdx);

        Music musicLikeUser = musicRepository.findByMusicIdxAndMusicLikesUsers(musicIdx, user);
        if(musicLikeUser==null){
            user.getMusicLikes().add(music);
            music.getMusicLikesUsers().add(user);
        }
        else{
            music.getMusicLikesUsers().remove(user);
            createMusicLike=false;
        }

        //가수 정보 저장
        try{
            music = musicRepository.save(music);
        } catch(Exception exception) {
            if(createMusicLike==true) {
                throw new BaseException(FAILED_TO_POST_MUSICLIKE);
            }
            else{
                throw new BaseException(FAILED_TO_DELETE_MUSICLIKE);
            }
        }

        return createMusicLike;
    }

}
