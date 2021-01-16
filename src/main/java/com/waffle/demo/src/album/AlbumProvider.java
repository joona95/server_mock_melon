package com.waffle.demo.src.album;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.utils.JwtService;
import com.waffle.demo.src.genre.GenreRepository;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.album.models.*;
import com.waffle.demo.src.music.models.Music;
//import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.singer.models.GetSingerChannelRes;
import com.waffle.demo.src.singer.models.Singer;
//import com.waffle.demo.src.singer.models.SingerLike;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.models.CurrentPlaylistMusic;
import com.waffle.demo.src.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class AlbumProvider {
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final JwtService jwtService;
    private final UserProvider userProvider;

    @Autowired
    public AlbumProvider(AlbumRepository albumRepository, GenreRepository genreRepository, JwtService jwtService, UserProvider userProvider) {
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.jwtService = jwtService;
        this.userProvider = userProvider;
    }

    /**
     * 전체 앨범 조회
     * @return List<GetAlbumsRes>
     * @throws BaseException
     */
    public List<GetAlbumsRes> retrieveAlbumList(String genrePar) throws BaseException{
        List<Album> albumList;
        try{
            //genrePar = genrePar.replaceAll("\"", "");
            if(genrePar!=null){
                Genre genre = genreRepository.findByGenreAndIsDeleted(genrePar, "N");
                albumList = albumRepository.findByGenres(genre);
            }
            else {
                albumList = albumRepository.findByIsDeleted("N");
            }
        } catch (Exception ignored){
            throw new BaseException(FAILED_TO_GET_ALBUM);
        }

        return albumList.stream().map(album -> {
            Integer albumIdx = album.getAlbumIdx();
            String albumTitle = album.getAlbumTitle();
            Date releaseDate = album.getReleaseDate();
            String albumImgUrl = album.getAlbumImgUrl();

            //평점 정보
            List<AlbumRate> albumRates = album.getAlbumRates();
            Integer albumRateUserCnt = 0;
            Float albumRate = 0F;
            if(albumRates.size()>0) {
                albumRateUserCnt = albumRates.size();
                for (int i = 0; i < albumRates.size(); i++) {
                    albumRate += albumRates.get(i).getAlbumRate();
                }
                albumRate /= albumRateUserCnt;
            }
            DecimalFormat format = new DecimalFormat("0.0");
            String albumRateAvg = format.format(albumRate);

            //가수정보
            List<Integer> singersIdx = new ArrayList<>();
            List<String> singersName = new ArrayList<>();
            for(int i=0;i<album.getSingers().size();i++){
                if(album.getSingers().get(i).getIsSinger().equals("Y")&&album.getSingers().get(i).getIsDeleted().equals("N")) {
                    Singer singer = album.getSingers().get(i).getSinger();
                    singersIdx.add(singer.getSingerIdx());
                    singersName.add(singer.getSingerName());
                }
            }

            //장르정보
            List<Integer> genresIdx = new ArrayList<>();
            List<String> genres = new ArrayList<>();
            for(int i=0;i<album.getGenres().size();i++){
                Genre genre = album.getGenres().get(i);
                genresIdx.add(genre.getGenreIdx());
                genres.add(genre.getGenre());
            }

            //음악정보
            List<Music> musics = album.getMusics();
            List<Integer> musicsIdx = new ArrayList<>();
            for(int i=0;i<musics.size();i++){
                if(musics.get(i).getIsDeleted().equals("N")) {
                    musicsIdx.add(musics.get(i).getMusicIdx());
                }
            }

            return new GetAlbumsRes(albumIdx, albumTitle, singersIdx, singersName, releaseDate, albumImgUrl, albumRateAvg, albumRateUserCnt, musicsIdx, genresIdx, genres);
        }).collect(Collectors.toList());
    }

    /**
     * 앨범 상세 조회
     * @param albumIdx
     * @return GetAlbumRes
     * @throws BaseException
     */
    public GetAlbumRes retrieveAlbum(Integer albumIdx) throws BaseException {
        Album album = retrieveAlbumByAlbumIdx(albumIdx);

        String albumTitle = album.getAlbumTitle();
        Date releaseDate = album.getReleaseDate();
        String albumImgUrl = album.getAlbumImgUrl();
        String releaseCompany = album.getReleaseCompany();
        String albumIntroduction = album.getAlbumIntroduction();
        String albumType = album.getAlbumType();
        String agency = album.getAgency();

        //가수정보
        List<Integer> singersIdx = new ArrayList<>();
        List<String> singersName = new ArrayList<>();
        for(int i=0;i<album.getSingers().size();i++){
            if(album.getSingers().get(i).getIsSinger().equals("Y")&&album.getSingers().get(i).getIsDeleted().equals("N")) {
                Singer singer = album.getSingers().get(i).getSinger();
                singersIdx.add(singer.getSingerIdx());
                singersName.add(singer.getSingerName());
            }
        }
        //String singerName = singer.stream().map(n -> String.valueOf(n.getSingerName())).collect(Collectors.joining(","));

        //장르정보
        List<Integer> genresIdx = new ArrayList<>();
        List<String> genres = new ArrayList<>();
        for(int i=0;i<album.getGenres().size();i++){
            Genre genre = album.getGenres().get(i);
            genresIdx.add(genre.getGenreIdx());
            genres.add(genre.getGenre());
        }

        //음악정보
        List<Music> musics = album.getMusics();
        List<Integer> musicsIdx = new ArrayList<>();
        List<String> musicsTitle = new ArrayList<>();
        List<String> writings = new ArrayList<>();
        List<String> composings = new ArrayList<>();
        List<String> arrangings = new ArrayList<>();
        List<String> lyrics = new ArrayList<>();
        for(int i=0;i<musics.size();i++){
            Music music = musics.get(i);
            if(music.getIsDeleted().equals("N")) {
                musicsIdx.add(music.getMusicIdx());
                musicsTitle.add(music.getMusicTitle());
                writings.add(music.getWriting());
                composings.add(music.getComposing());
                arrangings.add(music.getArranging());
                lyrics.add(music.getLyric());
            }
        }

        return new GetAlbumRes(albumIdx, albumTitle, singersIdx, singersName, releaseDate, albumImgUrl, releaseCompany, agency, albumIntroduction, albumType, genresIdx, genres, musicsIdx, musicsTitle, writings, composings, arrangings, lyrics);
    }


    /**
     * Idx로 앨범 조회
     * @param albumIdx
     * @return Album
     * @throws BaseException
     */
    public Album retrieveAlbumByAlbumIdx(Integer albumIdx) throws BaseException{
        Album album;
        try{
            album = albumRepository.findById(albumIdx).orElse(null);
        } catch (Exception ignored){
            throw new BaseException(FAILED_TO_GET_ALBUM);
        }

        if(album==null || !album.getIsDeleted().equals("N")){
            throw new BaseException(NOT_FOUND_ALBUM);
        }

        return album;
    }


    /**
     * 앨범 채널 조회
     * @param albumIdx
     * @return List<GetAlbumChannelRes>
     * @throws BaseException
     */

    public GetAlbumChannelRes retrieveAlbumChannel(Integer albumIdx) throws BaseException{

        Album album = retrieveAlbumByAlbumIdx(albumIdx);

        //앨범 주인인 가수들 찾아서 RES
        List<Integer> isSingersIdx=new ArrayList<>();
        List<String> isSingersName =new ArrayList<>();
        for(int i=0;i<album.getSingers().size();i++){
            if(album.getSingers().get(i).getIsSinger().equals("Y")&&album.getSingers().get(i).getIsDeleted().equals("N")) {
                Singer singer = album.getSingers().get(i).getSinger();
                isSingersIdx.add(singer.getSingerIdx());
                isSingersName.add(singer.getSingerName());
            }
        }

        String albumTitle = album.getAlbumTitle();
        Date releaseDate = album.getReleaseDate();
        String albumImgUrl = album.getAlbumImgUrl();

        //평점 정보
        List<AlbumRate> albumRates = album.getAlbumRates();
        Float albumRate = 0F;
        Integer albumRateUserCnt=0;
        if(albumRates.size()>0) {
            albumRateUserCnt = albumRates.size();
            for (int i = 0; i < albumRates.size(); i++) {
                albumRate += albumRates.get(i).getAlbumRate();
            }
            albumRate /= albumRateUserCnt;
        }
        DecimalFormat format = new DecimalFormat("0.0");
        String albumRateAvg = format.format(albumRate);

        //가수 프로필 사진
        String singerProfileUrl = "Various Artists";
        if(isSingersIdx.size()==1){
            Singer singer = album.getSingers().get(0).getSinger();
            singerProfileUrl = singer.getProfileImgUrl();
        }

        //앨범 좋아요
        String albumLike = "N";
        Integer albumLikeCnt = album.getAlbumLikesUsers().size();
        List<User> users = album.getAlbumLikesUsers();


        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserIdx() == jwtService.getUserIdx()){
                albumLike = "Y";
            }
        }

        return new GetAlbumChannelRes(albumIdx, albumTitle, isSingersIdx, isSingersName, releaseDate, albumImgUrl, albumRateAvg, albumRateUserCnt, singerProfileUrl, albumLike, albumLikeCnt);
    }


}
