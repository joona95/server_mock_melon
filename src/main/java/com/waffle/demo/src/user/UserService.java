package com.waffle.demo.src.user;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.secret.Secret;
import com.waffle.demo.config.utils.AES128;
//import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.music.MusicProvider;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.user.models.*;
import com.waffle.demo.config.utils.JwtService;
import com.waffle.demo.src.voucher.VoucherProvider;
import com.waffle.demo.src.voucher.models.Voucher;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMusicPlayRepository userMusicPlayRepository;
    private final CurrentPlaylistMusicRepository currentPlaylistMusicRepository;
    private final UserProvider userProvider;
    private final MusicProvider musicProvider;
    private final VoucherProvider voucherProvider;
    private final UserVoucherRepository userVoucherRepository;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository,MusicProvider musicProvider, UserMusicPlayRepository userMusicPlayRepository, CurrentPlaylistMusicRepository currentPlaylistMusicRepository, UserProvider userProvider, VoucherProvider voucherProvider, UserVoucherRepository userVoucherRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMusicPlayRepository = userMusicPlayRepository;
        this.currentPlaylistMusicRepository = currentPlaylistMusicRepository;
        this.userProvider = userProvider;
        this.musicProvider = musicProvider;
        this.voucherProvider = voucherProvider;
        this.userVoucherRepository = userVoucherRepository;
        this.jwtService = jwtService;
    }

    /**
     * 회원가입
     * @param postUserReq
     * @return PostUserRes
     * @throws BaseException
     */
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        User existsUser = null;
        try {
            // 1-1. 이미 존재하는 회원이 있는지 조회
            existsUser = userProvider.retrieveUserByUserId(postUserReq.getUserId());
        } catch (BaseException exception) {
            // 1-2. 이미 존재하는 회원이 없다면 그대로 진행
            if (exception.getStatus() != NOT_FOUND_USER) {
                throw exception;
            }
        }
        // 1-3. 이미 존재하는 회원이 있다면 return DUPLICATED_USER
        if (existsUser != null) {
            throw new BaseException(DUPLICATED_USER);
        }

        // 2. 유저 정보 생성
        String userId = postUserReq.getUserId();
        String nickname = postUserReq.getNickname();
        String phoneNum = postUserReq.getPhoneNum();
        String email = postUserReq.getEmail();
        String gender = postUserReq.getGender();
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_POST_USER);
        }
        User user = new User(userId, password, nickname, phoneNum, email, gender);

        // 3. 유저 정보 저장
        try {
            user = userRepository.save(user);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_USER);
        }

        // 4. JWT 생성
        String jwt = jwtService.createJwt(user.getUserIdx());


        // 5. UserInfoLoginRes로 변환하여 return
        Integer userIdx = user.getUserIdx();
        return new PostUserRes(userIdx, userId, nickname, jwt);
    }

    /**
     * 회원 정보 수정 (POST uri 가 겹쳤을때의 예시 용도)
     * @param patchUserReq
     * @return PatchUserRes
     * @throws BaseException
     */
    public PatchUserRes updateUser(@NonNull Integer userIdx, PatchUserReq patchUserReq) throws BaseException {
        //jwt 확인
        if(userIdx != jwtService.getUserIdx() && jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        User user = userProvider.retrieveUserByUserIdx(userIdx);

        //User에서 password 추출
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_PATCH_USER);
        }

        //비밀번호 일치 여부 확인
        if (!patchUserReq.getPassword().equals(password)) {
            throw new BaseException(WRONG_PASSWORD);
        }

        //유저 정보 수정
        user.setNickname(patchUserReq.getNickname());
        user.setPhoneNum(patchUserReq.getPhoneNum());
        user.setEmail(patchUserReq.getEmail());
        user.setAuthentication(patchUserReq.getAuthentication());
        user.setUserProfileUrl(patchUserReq.getUserProfileUrl());
        try {
            user = userRepository.save(user);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_USER);
        }

        try {
            String userId = user.getUserId();
            String nickname = user.getNickname();
            String phoneNumber = user.getPhoneNum();
            String email = user.getEmail();
            String grade = user.getGrade();
            String gender = user.getGender();
            String authentication = user.getAuthentication();
            String userProfileUrl = user.getUserProfileUrl();
            return new PatchUserRes(userIdx, userId, nickname, phoneNumber, email, grade, gender, authentication, userProfileUrl);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_PATCH_USER);
        }
    }

    /**
     * 회원 탈퇴
     * @param userIdx
     * @throws BaseException
     */
    public void deleteUser(Integer userIdx, DeleteUserReq deleteUserReq) throws BaseException {
        //jwt 확인
        if(userIdx != jwtService.getUserIdx() && jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        // 존재하는 UserInfo가 있는지 확인 후 저장
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        //User에서 password 추출
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_DELETE_USER);
        }

        //비밀번호 일치 여부 확인
        if (!deleteUserReq.getPassword().equals(password)) {
            throw new BaseException(WRONG_PASSWORD);
        }

        // 해당 User의 isDeleted를 Y로 설정
        user.setIsDeleted("Y");
        try {
            userRepository.save(user);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_DELETE_USER);
        }
    }

    /**
     * 유저 이용권 생성
     * @param postUserVoucherReq
     * @return PostUserVoucherRes
     * @throws BaseException
     */
    public PostUserVoucherRes createUserVoucher(PostUserVoucherReq postUserVoucherReq) throws BaseException {

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());

        List<UserVoucher> userVouchers;
        try {
            userVouchers = userVoucherRepository.findByUserAndVoucherEnd(user, "N");
        }catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_USERVOUCHER);
        }

        Integer voucherIdx = postUserVoucherReq.getVoucherIdx();
        Voucher voucher = voucherProvider.retrieveVoucherByVoucherIdx(voucherIdx);

        //boolean find = false;
        for(int i=0;i<userVouchers.size();i++){
            if(userVouchers.get(i).getVoucher().getHasStreaming().equals(voucher.getHasStreaming()) || userVouchers.get(i).getVoucher().getHasDownload().equals(voucher.getHasDownload())){
                throw new BaseException(DUPLICATED_USERVOUCHER);
            }
        }



        UserVoucher userVoucher = new UserVoucher(user, voucher);

        user.getUserVouchers().add(userVoucher);
        try {
            userVoucher = userVoucherRepository.save(userVoucher);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_USERVOUCHER);
        }

        Integer userVoucherIdx = userVoucher.getUserVoucherIdx();
        String voucherName = userVoucher.getVoucher().getVoucherName();
        Date voucherStartDate = userVoucher.getVoucherStartDate();
        Date voucherEndDate = userVoucher.getVoucherStartDate();
        String voucherStop = userVoucher.getVoucherStop();
        String voucherStopReason = userVoucher.getVoucherStopReason();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(voucherEndDate);
        cal.add(Calendar.MONTH, 1);

        String startDate = sdformat.format(voucherStartDate);
        String endDate = sdformat.format(cal.getTime());



        return new PostUserVoucherRes(userVoucherIdx, voucherName, startDate, endDate, voucherStop, voucherStopReason);
    }

    /**
     * 유저 이용권 삭제
     * @param userVoucherIdx
     * @throws BaseException
     */
    public void deleteUserVoucher(Integer userVoucherIdx) throws BaseException {
        UserVoucher userVoucher;
        try {
            userVoucher = userVoucherRepository.findById(userVoucherIdx).orElse(null);
        }catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USERVOUCHER);
        }

        if (userVoucher == null || !userVoucher.getVoucherEnd().equals("N")) {
            throw new BaseException(NOT_FOUND_USERVOUCHER);
        }

        if(userVoucher.getUser().getUserIdx()!= jwtService.getUserIdx()){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        userVoucher.setVoucherEnd("Y");
        try {
            userVoucherRepository.save(userVoucher);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_DELETE_USERVOUCHER);
        }
    }

    /**
     * 현재 재생 목록 곡 생성(변경)
     * @param postCurrentPlaylistReq
     * @return void
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public void createCurrentPlaylist(PostCurrentPlaylistReq postCurrentPlaylistReq) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        try {
            UserMusicPlay userMusicPlay = userMusicPlayRepository.findById(userIdx).orElse(null);
        }catch(Exception ignored) {
            throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
        }

        List<Integer> musicsIdx = postCurrentPlaylistReq.getMusicsIdx();
        Integer createType = postCurrentPlaylistReq.getCreateType();



        List<CurrentPlaylistMusic> currentPlaylistMusics;
        try {
            currentPlaylistMusics = currentPlaylistMusicRepository.findByUserAndIsDeletedOrderByOrder(user, "N");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
        }


        Integer startPosition = 0;
        Integer endPosition = 0;
        Integer currentPlaylistMusicCnt = 0;
        if(currentPlaylistMusics!=null){
            currentPlaylistMusicCnt = currentPlaylistMusics.size();
            startPosition = currentPlaylistMusics.get(0).getCurrentPlaylistMusicIdx();
            endPosition = currentPlaylistMusics.get(currentPlaylistMusicCnt-1).getCurrentPlaylistMusicIdx();
        }

        UserMusicPlay userMusicPlay = userMusicPlayRepository.findById(userIdx).orElse(null);

        Integer order = 0;
        if(createType==1&&userMusicPlay!=null&&userMusicPlay.getCurrentPosition()!=null&&userMusicPlay.getCurrentPosition()>0){
            Integer currentMusicIdx = userMusicPlay.getCurrentPosition();
            for(int i=0;i<currentPlaylistMusics.size();i++){
                CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusics.get(i);
                currentPlaylistMusics.get(i).setOrder(order);
                try{
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                }catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
                }
                order++;

                if(currentPlaylistMusics.get(i).getCurrentPlaylistMusicIdx()==currentMusicIdx){
                    for(int j=0;j<musicsIdx.size();j++){
                        Music music = musicProvider.retrieveMusicByMusicIdx(musicsIdx.get(j));
                        currentPlaylistMusic = new CurrentPlaylistMusic(user, music, order);
                        try{
                            currentPlaylistMusicRepository.save(currentPlaylistMusic);
                        }catch (Exception ignored) {
                            throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
                        }
                        order++;
                    }
                }

            }

        }
        else if(createType==0){
            for(int i=0;i<musicsIdx.size();i++){
                Music music = musicProvider.retrieveMusicByMusicIdx(musicsIdx.get(i));
                CurrentPlaylistMusic currentPlaylistMusic = new CurrentPlaylistMusic(user, music, order);
                try{
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                }catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
                }
                order++;
            }

            for(int i=0;i<currentPlaylistMusics.size();i++){
                CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusics.get(i);
                currentPlaylistMusics.get(i).setOrder(order);
                try{
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                }catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
                }
                order++;
            }
        }
        else{
            for(int i=0;i<currentPlaylistMusics.size();i++){
                CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusics.get(i);
                currentPlaylistMusics.get(i).setOrder(order);
                try{
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                }catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
                }
                order++;
            }

            for(int i=0;i<musicsIdx.size();i++){
                Music music = musicProvider.retrieveMusicByMusicIdx(musicsIdx.get(i));
                CurrentPlaylistMusic currentPlaylistMusic = new CurrentPlaylistMusic(user, music, order);
                try{
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                }catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_POST_CURRENTPLAYLIST);
                }
                order++;
            }
        }

    }

    /**
     * 현재 재생 목록 곡 삭제
     * @param
     * @throws BaseException
     */
    public void deleteCurrentPlaylist(DeleteCurrentPlaylistReq deleteCurrentPlaylistReq) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());

        List<Integer> musicsIdx = deleteCurrentPlaylistReq.getCurrentPlaylistMusicsIdx();

        for(int i=0;i<musicsIdx.size();i++){
            CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusicRepository.findById(musicsIdx.get(i)).orElse(null);

            if(currentPlaylistMusic!=null&&currentPlaylistMusic.getIsDeleted().equals("N")){
                if(currentPlaylistMusic.getUser().getUserIdx()!=user.getUserIdx())
                    throw new BaseException(FAILED_TO_ACCESS);

                currentPlaylistMusic.setIsDeleted("Y");

                try {
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                } catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_DELETE_CURRENTPLAYLIST);
                }
            }

        }

    }


    /**
     * 현재 재생 목록 곡 순서 변경
     * @param patchCurrentPlaylistReq
     * @return void
     * @throws BaseException
     */
    @Transactional(rollbackFor = {Exception.class})
    public void updateCurrentPlaylist(PatchCurrentPlaylistReq patchCurrentPlaylistReq) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        List<Integer> musicsIdx = patchCurrentPlaylistReq.getCurrentPlaylistMusicsIdx();

        List<CurrentPlaylistMusic> currentPlaylistMusicList = currentPlaylistMusicRepository.findByUserAndIsDeletedOrderByOrder(user, "N");

        if(musicsIdx.size()!=currentPlaylistMusicList.size()){
            throw new BaseException(DO_NOT_MATCH_MUSICCNT);
        }

        for(int i=0;i<musicsIdx.size();i++) {
            CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusicRepository.findById(musicsIdx.get(i)).orElse(null);

            if(currentPlaylistMusic==null||currentPlaylistMusic.getIsDeleted().equals("Y")){
                throw new BaseException(FAILED_TO_PATCH_CURRENTPLAYLIST);
            }

            if(currentPlaylistMusic.getUser().getUserIdx() == userIdx) {
                currentPlaylistMusic.setOrder(i);

                try {
                    currentPlaylistMusicRepository.save(currentPlaylistMusic);
                } catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_PATCH_CURRENTPLAYLIST);
                }
            }
            else{
                throw new BaseException(FAILED_TO_ACCESS);
            }

        }

    }


    /**
     * 현재 재생 곡 선택
     * @param postCurrentPlayMusicReq
     * @return void
     * @throws BaseException
     */
    public void createCurrentPlayMusic(PostCurrentPlayMusicReq postCurrentPlayMusicReq) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        UserMusicPlay userMusicPlay = userMusicPlayRepository.findById(userIdx).orElse(null);

        Integer currentPlaylistMusicIdx = postCurrentPlayMusicReq.getCurrentPlaylistMusicIdx();

        List<CurrentPlaylistMusic> currentPlaylistMusicList = currentPlaylistMusicRepository.findByUserAndIsDeletedOrderByOrder(user, "N");
        CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusicRepository.findById(currentPlaylistMusicIdx).orElse(null);
        if(currentPlaylistMusic==null)
            throw new BaseException(FAILED_TO_POST_CURRENTPLAYMUSIC);

        if(currentPlaylistMusic.getUser().getUserIdx()!=userIdx)
            throw new BaseException(FAILED_TO_ACCESS);

        Integer startPosition=0;
        Integer endPosition=0;
        if(currentPlaylistMusicList!=null && currentPlaylistMusicList.size()>0){
            startPosition = currentPlaylistMusicList.get(0).getCurrentPlaylistMusicIdx();
            Integer size = currentPlaylistMusicList.size();
            endPosition = currentPlaylistMusicList.get(size-1).getCurrentPlaylistMusicIdx();
        }

        if(userMusicPlay==null){
            userMusicPlay = new UserMusicPlay(userIdx,startPosition,endPosition,currentPlaylistMusicIdx);

        }
        else{
            userMusicPlay.setStartPosition(startPosition);
            userMusicPlay.setLastPosition(endPosition);
            userMusicPlay.setCurrentPosition(currentPlaylistMusicIdx);
        }

        try {
            userMusicPlayRepository.save(userMusicPlay);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_POST_CURRENTPLAYMUSIC);
        }
    }
}
