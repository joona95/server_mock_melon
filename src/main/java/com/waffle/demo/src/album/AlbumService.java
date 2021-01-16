package com.waffle.demo.src.album;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.src.album.models.*;
import com.waffle.demo.src.genre.GenreProvider;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.singer.SingerProvider;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.config.utils.JwtService;
//import com.waffle.demo.src.singer.models.SingerLike;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.models.User;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.soap.SOAPBinding;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumSingerRepository albumSingerRepository;
    private final AlbumRateRepository albumRateRepository;
    private final UserProvider userProvider;
    private final AlbumProvider albumProvider;
    private final SingerProvider singerProvider;
    private final GenreProvider genreProvider;
    private final JwtService jwtService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumSingerRepository albumSingerRepository, AlbumRateRepository albumRateRepository, UserProvider userProvider, AlbumProvider albumProvider, SingerProvider singerProvider, GenreProvider genreProvider, JwtService jwtService){
        this.albumRepository = albumRepository;
        this.albumSingerRepository = albumSingerRepository;
        this.albumRateRepository = albumRateRepository;
        this.userProvider = userProvider;
        this.albumProvider = albumProvider;
        this.singerProvider = singerProvider;
        this.genreProvider = genreProvider;
        this.jwtService = jwtService;
    }

    /**
     * 앨범 등록 API
     * @param postAlbumReq
     * @return PostAlbumRes
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public PostAlbumRes createAlbum(PostAlbumReq postAlbumReq) throws BaseException {
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        //앨범은 이미 있는지 없는지 확인할 필요가 없음
        //앨범 정보 생성
        String albumTitle = postAlbumReq.getAlbumTitle();
        Date releaseDate = Date.valueOf(postAlbumReq.getReleaseDate());
        String albumImgUrl = postAlbumReq.getAlbumImgUrl();
        String releaseCompany = postAlbumReq.getReleaseCompany();
        String agency = postAlbumReq.getAgency();
        String albumIntroduction = postAlbumReq.getAlbumIntroduction();
        String albumType = postAlbumReq.getAlbumType();

        Album album = new Album(albumTitle, releaseDate, albumImgUrl, releaseCompany, agency, albumIntroduction, albumType);

        List<Integer> genresIdx = postAlbumReq.getGenresIdx();
        for(int i=0;i<genresIdx.size();i++){
            Genre genre = genreProvider.retrieveGenreByGenreIdx(genresIdx.get(i));
            album.getGenres().add(genre);
            genre.getAlbums().add(album);
        }

        try{
            album = albumRepository.save(album);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_ALBUM);
        }

        List<Integer> singersIdx = postAlbumReq.getSingersIdx();
        List<String> types = postAlbumReq.getTypes();
        List<String> isSingers = postAlbumReq.getIsSingers();

        for(int i=0;i<singersIdx.size();i++){
            Singer singer = singerProvider.retrieveSingerBySingerIdx(singersIdx.get(i));
            AlbumSinger albumSinger= new AlbumSinger(album, singer, types.get(i), isSingers.get(i));
            try{
                albumSinger = albumSingerRepository.save(albumSinger);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_POST_ALBUM);
            }
            album.getSingers().add(albumSinger);
            singer.getAlbumSingers().add(albumSinger);
        }


        Integer albumIdx = album.getAlbumIdx();

        return new PostAlbumRes(albumIdx);
    }

    /**
     * 앨범 정보 수정 (POST uri 가 겹쳤을때의 예시 용도)
     * @param patchAlbumReq
     * @return PatchAlbumRes
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public PatchAlbumRes updateAlbum(@NonNull Integer albumIdx, PatchAlbumReq patchAlbumReq) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Album album = albumProvider.retrieveAlbumByAlbumIdx(albumIdx);

        //유저 정보 수정
        album.setAlbumTitle(patchAlbumReq.getAlbumTitle());
        album.setReleaseDate(Date.valueOf(patchAlbumReq.getReleaseDate()));
        album.setAlbumImgUrl(patchAlbumReq.getAlbumImgUrl());
        album.setReleaseCompany(patchAlbumReq.getReleaseCompany());
        album.setAgency(patchAlbumReq.getAgency());
        album.setAlbumIntroduction(patchAlbumReq.getAlbumIntroduction());
        album.setAlbumType(patchAlbumReq.getAlbumType());


        List<Integer> genresIdx = patchAlbumReq.getGenresIdx();
        album.getGenres().clear();
        for(int i=0;i<genresIdx.size();i++){
            Genre genre = genreProvider.retrieveGenreByGenreIdx(genresIdx.get(i));
            genre.getAlbums().remove(album);
            album.getGenres().add(genre);
            genre.getAlbums().add(album);
        }

        try{
            album = albumRepository.save(album);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_ALBUM);
        }


        for(int i=0;i<album.getSingers().size();i++){
            AlbumSinger albumSinger = album.getSingers().get(i);
            albumSinger.setIsDeleted("Y");
            try{
                albumSinger = albumSingerRepository.save(albumSinger);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_PATCH_ALBUM);
            }
        }

        List<Integer> singersIdx = patchAlbumReq.getSingersIdx();
        List<String> types = patchAlbumReq.getTypes();
        List<String> isSingers = patchAlbumReq.getIsSingers();

        for(int i=0;i<singersIdx.size();i++){
            Singer singer = singerProvider.retrieveSingerBySingerIdx(singersIdx.get(i));
            AlbumSinger albumSinger= albumSingerRepository.findByAlbumAndSinger(album, singer);
            if(albumSinger== null) {
                albumSinger = new AlbumSinger(album, singer, types.get(i), isSingers.get(i));
            }
            else{
                albumSinger.setIsDeleted("N");
            }

            try{
                albumSingerRepository.save(albumSinger);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_PATCH_ALBUM);
            }
        }


        return new PatchAlbumRes(albumIdx);

    }

    /**
     * 앨범 삭제
     * @param albumIdx
     * @throws BaseException
    */
    public void deleteAlbum(Integer albumIdx) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Album album = albumProvider.retrieveAlbumByAlbumIdx(albumIdx);

        album.setIsDeleted("Y");
        try{
            albumRepository.save(album);
        } catch(Exception ignored){
            throw new BaseException(FAILED_TO_DELETE_ALBUM);
        }
    }

    /**
     * 앨범 좋아요 생성/취소
     * @param albumIdx
     * @throws BaseException
     * @return boolean
     */
    public boolean createOrDeleteAlbumLike(Integer albumIdx) throws BaseException {

        boolean createAlbumLike = true;

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());
        Album album = albumProvider.retrieveAlbumByAlbumIdx(albumIdx);

        Album albumLikeUser = albumRepository.findByAlbumIdxAndAlbumLikesUsers(albumIdx, user);

        if(albumLikeUser==null){
            user.getAlbumLikes().add(album);
            album.getAlbumLikesUsers().add(user);
        }
        else{
            album.getAlbumLikesUsers().remove(user);
            createAlbumLike=false;
        }

        //앨범 정보 저장
        try{
            album = albumRepository.save(album);
        } catch(Exception exception) {
            if(createAlbumLike==true) {
                throw new BaseException(FAILED_TO_POST_ALBUMLIKE);
            }
            else{
                throw new BaseException(FAILED_TO_DELETE_ALBUMLIKE);
            }
        }

        return createAlbumLike;
    }

    /**
     * 앨범 평점 생성
     * @param albumIdx
     * @throws BaseException
     */
    public void createAlbumRate(@NonNull Integer albumIdx, PostAlbumRateReq postAlbumRateReq) throws BaseException{

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());
        Album album = albumProvider.retrieveAlbumByAlbumIdx(albumIdx);

        AlbumRate albumRate = albumRateRepository.findByAlbumAndUser(album, user);
        if(albumRate!=null){
            throw new BaseException(DUPLICATED_ALBUMRATE);
        }

        String r = String.format("%.1f", postAlbumRateReq.getRate());
        Float rate = Float.parseFloat(r);
        albumRate = new AlbumRate(album, user, rate);


        try{
            albumRateRepository.save(albumRate);
        } catch(Exception ignored){
            throw new BaseException(FAILED_TO_POST_ALBUMRATE);
        }
    }
}
