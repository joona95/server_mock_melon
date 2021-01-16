package com.waffle.demo.src.singer;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.utils.JwtService;
import com.waffle.demo.src.genre.GenreProvider;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.music.MusicProvider;
import com.waffle.demo.src.music.MusicSingerRepository;
import com.waffle.demo.src.music.models.Music;
//import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.singer.models.*;
import com.waffle.demo.src.user.CurrentPlaylistMusicRepository;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.models.User;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class SingerService {
    private final SingerRepository singerRepository;
    private final MusicSingerRepository musicSingerRepository;
    private final SingerCommentRepository singerCommentRepository;
    private final SingerCommentLikeRepository singerCommentLikeRepository;
    private final CurrentPlaylistMusicRepository currentPlaylistMusicRepository;
    private final SingerProvider singerProvider;
    private final UserProvider userProvider;
    private final MusicProvider musicProvider;
    private final GenreProvider genreProvider;
    private final JwtService jwtService;

    @Autowired
    public SingerService(SingerRepository singerRepository, SingerCommentLikeRepository singerCommentLikeRepository, MusicSingerRepository musicSingerRepository, SingerCommentRepository singerCommentRepository, CurrentPlaylistMusicRepository currentPlaylistMusicRepository, SingerProvider singerProvider, UserProvider userProvider, MusicProvider musicProvider, GenreProvider genreProvider, JwtService jwtService){
        this.singerProvider = singerProvider;
        this.singerCommentLikeRepository = singerCommentLikeRepository;
        this.musicSingerRepository = musicSingerRepository;
        this.singerCommentRepository = singerCommentRepository;
        this.singerRepository = singerRepository;
        this.currentPlaylistMusicRepository = currentPlaylistMusicRepository;
        this.userProvider = userProvider;
        this.musicProvider = musicProvider;
        this.genreProvider = genreProvider;
        this.jwtService = jwtService;
    }

    /**
     * 가수 등록
     * @param postSingerReq
     * @return PostSingerRes
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public PostSingerRes createSinger(PostSingerReq postSingerReq) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        //가수 정보 생성
        String singerName = postSingerReq.getSingerName();
        String channelName = postSingerReq.getChannelName();
        Integer profileMusicIdx = postSingerReq.getProfileMusicIdx();
        String profileImgUrl = postSingerReq.getProfileImgUrl();
        String nationality = postSingerReq.getNationality();
        String singerType = postSingerReq.getSingerType();
        String gender = postSingerReq.getGender();
        String agency = postSingerReq.getAgency();
        String career = postSingerReq.getCareer();
        String singerIntroduction = postSingerReq.getSingerIntroduction();
        String officialSite = postSingerReq.getOfficialSite();
        String facebook = postSingerReq.getFacebook();
        String twitter = postSingerReq.getTwitter();
        String instagram = postSingerReq.getInstagram();

        String profileMusicTitle="";
        List<String> profileMusicSingersName = new ArrayList<>();
        if(profileMusicIdx!=null&&profileMusicIdx>0) {
            Music music = musicProvider.retrieveMusicByMusicIdx(profileMusicIdx);
            profileMusicTitle = music.getMusicTitle();
            List<MusicSinger> musicSingers = musicSingerRepository.findByMusicAndIsSinger(music, "Y");
            for(int i=0;i<musicSingers.size();i++){
                profileMusicSingersName.add(musicSingers.get(i).getSinger().getSingerName());
            }

        }

        Singer singer = new Singer(singerName, channelName, profileMusicIdx, profileImgUrl, nationality, gender, singerType, agency, career, singerIntroduction, officialSite, facebook, twitter, instagram);

        List<Integer> genresIdx = postSingerReq.getGenresIdx();
        for(int i=0;i<genresIdx.size();i++){
            Genre genre = genreProvider.retrieveGenreByGenreIdx(genresIdx.get(i));
            singer.getGenres().add(genre);
        }

        //가수 정보 저장
        try{
            singer = singerRepository.save(singer);
        } catch(Exception exception) {
            throw new BaseException(FAILED_TO_POST_SINGER);
        }


        List<Integer> membersIdx = postSingerReq.getMembersIdx();
        if(membersIdx!=null) {
            for (int i = 0; i < membersIdx.size(); i++) {
                Singer member = singerProvider.retrieveSingerBySingerIdx(membersIdx.get(i));
                singer.getMemberLists().add(member);
                member.getGroupLists().add(singer);

                //가수 정보 저장
                try{
                    singer = singerRepository.save(singer);
                } catch(Exception exception) {
                    throw new BaseException(FAILED_TO_POST_SINGER);
                }
            }
        }

        List<Integer> groupsIdx = postSingerReq.getGroupsIdx();
        if(groupsIdx!=null){
            for(int i=0;i<groupsIdx.size();i++){
                Singer group = singerProvider.retrieveSingerBySingerIdx(groupsIdx.get(i));
                group.getMemberLists().add(singer);
                singer.getGroupLists().add(group);

                //가수 정보 저장
                try{
                    group = singerRepository.save(group);
                } catch(Exception exception) {
                    throw new BaseException(FAILED_TO_POST_SINGER);
                }
            }
        }


        Integer singerIdx = singer.getSingerIdx();


        return new PostSingerRes(singerIdx);
    }

    /**
     * 가수 정보 수정 (POST uri 가 겹쳤을때의 예시 용도)
     * @param patchSingerReq
     * @return PatchSingerRes
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public PatchSingerRes updateSinger(@NonNull Integer singerIdx, PatchSingerReq patchSingerReq) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        singer.setSingerName(patchSingerReq.getSingerName());
        singer.setChannelName(patchSingerReq.getChannelName());

        Music music = musicProvider.retrieveMusicByMusicIdx(patchSingerReq.getProfileMusicIdx());
        MusicSinger musicSinger = musicSingerRepository.findByMusicAndSinger(music, singer);
        if(musicSinger==null){
            throw new BaseException(FAILED_TO_PATCH_SINGER);
        }

        singer.setProfileMusicIdx(patchSingerReq.getProfileMusicIdx());
        singer.setProfileImgUrl(patchSingerReq.getProfileImgUrl());
        singer.setNationality(patchSingerReq.getNationality());
        singer.setGender(patchSingerReq.getGender());
        singer.setSingerType(patchSingerReq.getSingerType());
        singer.setAgency(patchSingerReq.getAgency());
        singer.setCareer(patchSingerReq.getCareer());
        singer.setSingerIntroduction(patchSingerReq.getSingerIntroduction());
        singer.setOfficialSite(patchSingerReq.getOfficialSite());
        singer.setFacebook(patchSingerReq.getFacebook());
        singer.setTwitter(patchSingerReq.getTwitter());
        singer.setInstagram(patchSingerReq.getInstagram());

        List<Integer> genresIdx = patchSingerReq.getGenresIdx();
        singer.getGenres().clear();
        for(int i=0;i<genresIdx.size();i++){
            Genre genre = genreProvider.retrieveGenreByGenreIdx(genresIdx.get(i));
            genre.getSingers().remove(singer);
            singer.getGenres().add(genre);
            genre.getSingers().add(singer);
        }


        //가수 정보 저장
        try{
            singer = singerRepository.save(singer);
        } catch(Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_SINGER);
        }


        List<Integer> membersIdx = patchSingerReq.getMembersIdx();
        singer.getMemberLists().clear();
        if(membersIdx!=null) {
            for (int i = 0; i < membersIdx.size(); i++) {
                Singer member = singerProvider.retrieveSingerBySingerIdx(membersIdx.get(i));
                member.getGroupLists().remove(singer);
                singer.getMemberLists().add(member);
                member.getGroupLists().add(singer);
            }
        }

        List<Integer> groupsIdx = patchSingerReq.getGroupsIdx();

        for(int i=0;i<singer.getGroupLists().size();i++){
            Singer s = singerProvider.retrieveSingerBySingerIdx(singer.getGroupLists().get(i).getSingerIdx());
            s.getMemberLists().remove(singer);
        }

        //가수 정보 저장
        try{
            singer = singerRepository.save(singer);
        } catch(Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_SINGER);
        }

        if(groupsIdx!=null){
            for(int i=0;i<groupsIdx.size();i++){
                Singer group = singerProvider.retrieveSingerBySingerIdx(groupsIdx.get(i));
                group.getMemberLists().remove(singer);
                group.getMemberLists().add(singer);
                singer.getGroupLists().add(group);

                //가수 정보 저장
                try{
                    group = singerRepository.save(group);
                } catch(Exception exception) {
                    throw new BaseException(FAILED_TO_PATCH_SINGER);
                }
            }
        }

        return new PatchSingerRes(singerIdx);

    }

    /**
     * 가수 삭제
     * @param singerIdx
     * @throws BaseException
     */
    public void deleteSinger(Integer singerIdx) throws BaseException{
        //jwt 확인 (admin인지)
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        singer.setIsDeleted("Y");
        try{
            singerRepository.save(singer);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_SINGER);
        }
    }

    /**
     * 가수 좋아요 생성/취소
     * @param singerIdx
     * @throws BaseException
     * @return boolean
     */
    public boolean createOrDeleteSingerLike(Integer singerIdx) throws BaseException {

        boolean createSingerLike = true;

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());
        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        Singer singerLikeUser = singerRepository.findBySingerIdxAndSingerLikesUsers(singerIdx, user);
        if(singerLikeUser==null){
            user.getSingerLikes().add(singer);
            singer.getSingerLikesUsers().add(user);
        }
        else{
            singer.getSingerLikesUsers().remove(user);
            createSingerLike=false;
        }

        //가수 정보 저장
        try{
            singer = singerRepository.save(singer);
        } catch(Exception exception) {
            if(createSingerLike==true) {
                throw new BaseException(FAILED_TO_POST_SINGERLIKE);
            }
            else{
                throw new BaseException(FAILED_TO_DELETE_SINGERLIKE);
            }
        }

        return createSingerLike;
    }

    /**
     * 가수 댓글 생성
     * @param postSingerCommentReq
     * @return PostSingerCommentRes
     * @throws BaseException
     */
    public PostSingerCommentRes createSingerComment(Integer singerIdx, PostSingerCommentReq postSingerCommentReq) throws BaseException{
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        String comment = postSingerCommentReq.getSingerComment();
        Integer musicIdx = postSingerCommentReq.getMusicIdx();
        String photoUrl = postSingerCommentReq.getPhotoUrl();

        SingerComment singerComment  = new SingerComment(singer, user, comment);
        singerComment.setMusicIdx(musicIdx);
        singerComment.setPhotoUrl(photoUrl);

        singer.getSingerComments().add(singerComment);
        user.getSingerComments().add(singerComment);

        //가수 정보 저장
        try{
            singerCommentRepository.save(singerComment);
        } catch(Exception exception) {
            throw new BaseException(FAILED_TO_POST_SINGERCOMMENT);
        }

        return new PostSingerCommentRes(singerIdx);
    }

    /**
     * 가수 댓글/대댓글 수정 (POST uri 가 겹쳤을때의 예시 용도)
     * @param patchSingerCommentReq
     * @return PatchSingerCommentRes
     * @throws BaseException
     */
    public PatchSingerCommentRes updateSingerComment(@NonNull Integer singerIdx, @NonNull Integer commentIdx, PatchSingerCommentReq patchSingerCommentReq) throws BaseException{
        Integer userIdx = jwtService.getUserIdx();

        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        SingerComment singerComment;
        try {
            singerComment = singerCommentRepository.findById(commentIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
        }

        if(singerComment!=null&&singerComment.getIsDeleted().equals("N") && singerComment.getUser().getUserIdx()==userIdx) {
            singerComment.setSingerComment(patchSingerCommentReq.getSingerComment());
            if (singerComment.getSingerCommentIdxParent() == null || singerComment.getSingerCommentIdxParent() <= 0) {
                singerComment.setMusicIdx(patchSingerCommentReq.getMusicIdx());
                singerComment.setPhotoUrl(patchSingerCommentReq.getPhotoUrl());
            }

            try {
                singerCommentRepository.save(singerComment);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_PATCH_SINGERCOMMENT);
            }

            return new PatchSingerCommentRes(singerIdx, commentIdx);


        }
        else{
            throw new BaseException(FAILED_TO_ACCESS);
        }
    }

    /**
     * 가수 댓글/대댓글 삭제
     * @param singerIdx, commentIdx
     * @throws BaseException
     */
    public void deleteSingerComment(Integer singerIdx, Integer commentIdx) throws BaseException{
        Integer userIdx= jwtService.getUserIdx();

        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        SingerComment singerComment;
        try {
            singerComment = singerCommentRepository.findById(commentIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_DELETE_SINGERCOMMENT);
        }

        if(singerComment!=null&&singerComment.getIsDeleted().equals("N")) {
            singerComment.setIsDeleted("Y");

            try {
                singerCommentRepository.save(singerComment);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_DELETE_SINGERCOMMENT);
            }

            List<SingerComment> singerRecomments = singerCommentRepository.findBySingerCommentIdxParentAndIsDeleted(commentIdx,"N");
            for(int i=0;i<singerRecomments.size();i++) {
                singerRecomments.get(i).setIsDeleted("Y");
                try {
                    singerCommentRepository.save(singerRecomments.get(i));
                } catch (Exception exception) {
                    throw new BaseException(FAILED_TO_DELETE_SINGERCOMMENT);
                }
            }
        }
    }

    /**
     * 가수 대댓글 생성
     * @return List<PostSingerReCommentRes>
     * @throws BaseException
     */
    public PostSingerReCommentRes createSingerReCommentList(Integer singerIdx, Integer commentIdx, PostSingerReCommentReq postSingerReCommentReq) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);

        String comment = postSingerReCommentReq.getSingerComment();

        SingerComment parent = singerCommentRepository.findById(commentIdx).orElse(null);

        if(parent!=null&&parent.getIsDeleted().equals("N")&&(parent.getSingerCommentIdxParent()==null||parent.getSingerCommentIdxParent()<=0)) {

            SingerComment singerComment = new SingerComment(singer, user, comment, commentIdx);

            singer.getSingerComments().add(singerComment);
            user.getSingerComments().add(singerComment);

            try {
                singerCommentRepository.save(singerComment);
            } catch (Exception ignored) {
                throw new BaseException(FAILED_TO_POST_SINGERRECOMMENT);
            }

            return new PostSingerReCommentRes(singerIdx, commentIdx);

        }
        else{
            throw new BaseException(FAILED_TO_POST_SINGERRECOMMENT);
        }
    }


    /**
     * 가수 댓글 좋아요 생성/취소
     * @param singerIdx, commentIdx
     * @throws BaseException
     * @return boolean
    */
    public boolean createOrDeleteSingerCommentLike(Integer singerIdx, Integer commentIdx, PostSingerCommentLikeReq postSingerCommentLikeReq) throws BaseException {

        boolean createSingerLike = true;

        String like = postSingerCommentLikeReq.getLike();
        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());
        Singer singer = singerProvider.retrieveSingerBySingerIdx(singerIdx);
        SingerComment singerComment;
        try {
            singerComment = singerCommentRepository.findById(commentIdx).orElse(null);
        } catch (Exception ignored) {
            if(like.equals("L"))
                throw new BaseException(FAILED_TO_POST_SINGERCOMMENTLIKE);
            else
                throw new BaseException(FAILED_TO_POST_SINGERCOMMENTHATE);
        }


        if(singerComment!=null && singerComment.getIsDeleted().equals("N")&& (singerComment.getSingerCommentIdxParent()==null || singerComment.getSingerCommentIdxParent()<=0)) {
            SingerCommentLike singerCommentLike = singerCommentLikeRepository.findByUserAndSingerComment(user, singerComment);

            if (singerCommentLike == null) {
                singerCommentLike = new SingerCommentLike(singerComment, user, like);
                singerComment.getSingerCommentLikes().add(singerCommentLike);

            } else {
                if (singerCommentLike.getIsDeleted().equals("N")) {
                    if (singerCommentLike.getLike().equals(like)) {
                        singerCommentLike.setIsDeleted("Y");
                        createSingerLike = false;
                    } else {
                        if (like.equals("L")) {
                            throw new BaseException(ALREADY_HATE);
                        } else {
                            throw new BaseException(ALREADY_LIKE);
                        }
                    }
                } else {
                    singerCommentLike.setIsDeleted("N");
                    if (!singerCommentLike.getLike().equals(like)) {
                        singerCommentLike.setLike(like);
                    }
                }
            }

            //가수 정보 저장
            try {
                singerCommentLikeRepository.save(singerCommentLike);
            } catch (Exception exception) {
                if (createSingerLike == true) {
                    if (like.equals("L"))
                        throw new BaseException(FAILED_TO_POST_SINGERCOMMENTLIKE);
                    else
                        throw new BaseException(FAILED_TO_POST_SINGERCOMMENTHATE);
                } else {
                    if (like.equals("L"))
                        throw new BaseException(FAILED_TO_DELETE_SINGERCOMMENTLIKE);
                    else
                        throw new BaseException(FAILED_TO_DELETE_SINGERCOMMENTHATE);
                }
            }

            return createSingerLike;
        }
        else{
            if(like.equals("L"))
                throw new BaseException(FAILED_TO_POST_SINGERCOMMENTLIKE);
            else
                throw new BaseException(FAILED_TO_POST_SINGERCOMMENTHATE);
        }
    }
}
