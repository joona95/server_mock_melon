package com.waffle.demo.src.user;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.secret.Secret;
import com.waffle.demo.config.utils.AES128;
import com.waffle.demo.config.utils.JwtService;
//import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.user.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class UserProvider {
    private final UserRepository userRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final UserMusicPlayRepository userMusicPlayRepository;
    private final JwtService jwtService;
    private final CurrentPlaylistMusicRepository currentPlaylistMusicRepository;

    @Autowired
    public UserProvider(UserRepository userRepository, UserMusicPlayRepository userMusicPlayRepository, UserVoucherRepository userVoucherRepository, JwtService jwtService, CurrentPlaylistMusicRepository currentPlaylistMusicRepository) {
        this.jwtService = jwtService;
        this.userMusicPlayRepository = userMusicPlayRepository;
        this.userVoucherRepository = userVoucherRepository;
        this.userRepository = userRepository;
        this.currentPlaylistMusicRepository = currentPlaylistMusicRepository;
    }

    /**
     * 전체 회원 조회
     *
     * @return List<GetUserRes>
     * @throws BaseException
     */
    public List<GetUserRes> retrieveUserList(String id, String genderPar, String gradePar) throws BaseException {
        //jwt 확인
        if (jwtService.getUserIdx() != 1) {
            throw new BaseException(FAILED_TO_ACCESS);
        }

        // 1. DB에서 전체 User 조회
        List<User> userList = new ArrayList<>();
        if (id != null) {
            User user = retrieveUserByUserId(id);
            if (user != null) {
                userList.add(user);
            }
        } else if (genderPar == null && gradePar == null) {
            userList = userRepository.findByIsDeleted("N");
        } else if (gradePar == null && genderPar != null) {
            userList = userRepository.findByGenderAndIsDeleted(genderPar, "N");
        } else if (genderPar == null && gradePar != null) {
            //gradePar = gradePar.replaceAll("\"","");
            userList = userRepository.findByGradeAndIsDeleted(gradePar, "N");
        } else {
            userList = userRepository.findByGenderAndGradeAndIsDeleted(genderPar, gradePar, "N");
        }

        // 2. GetUserRes로 변환하여 return
        return userList.stream().map(user -> {
            Integer userIdx = user.getUserIdx();
            String userId = user.getUserId();
            String nickname = user.getNickname();
            String phoneNum = user.getPhoneNum();
            String email = user.getEmail();
            String grade = user.getGrade();
            String gender = user.getGender();
            String authentication = user.getAuthentication();
            String userProfileUrl = user.getUserProfileUrl();
            return new GetUserRes(userIdx, userId, nickname, phoneNum, email, grade, gender, authentication, userProfileUrl);
        }).collect(Collectors.toList());
    }


    /**
     * 회원 상세 조회
     *
     * @param userIdx
     * @return GetUserRes
     * @throws BaseException
     */
    public GetUserRes retrieveUser(Integer userIdx) throws BaseException {
        //jwt 확인
        if (userIdx != jwtService.getUserIdx() && jwtService.getUserIdx() != 1) {
            throw new BaseException(FAILED_TO_ACCESS);
        }

        // 1. DB에서 userId로 UserInfo 조회
        User user = retrieveUserByUserIdx(userIdx);

        // 2. GetUserRes로 변환하여 return
        String userId = user.getUserId();
        String nickname = user.getNickname();
        String phoneNum = user.getPhoneNum();
        String email = user.getEmail();
        String grade = user.getGrade();
        String gender = user.getGender();
        String authentication = user.getAuthentication();
        String userProfileUrl = user.getUserProfileUrl();
        return new GetUserRes(userIdx, userId, nickname, phoneNum, email, grade, gender, authentication, userProfileUrl);
    }

    /**
     * Idx로 회원 조회
     *
     * @param userIdx
     * @return User
     * @throws BaseException
     */
    public User retrieveUserByUserIdx(Integer userIdx) throws BaseException {
        // 1. DB에서 User 조회
        User user;
        try {
            user = userRepository.findById(userIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USER);
        }

        // 2. 존재하는 회원인지 확인
        if (user == null || !user.getIsDeleted().equals("N")) {
            throw new BaseException(NOT_FOUND_USER);
        }

        // 3. User를 return
        return user;
    }


    /**
     * Id로 회원 조회
     *
     * @param userId
     * @return User
     * @throws BaseException
     */
    public User retrieveUserByUserId(String userId) throws BaseException {
        // 1. DB에서 User 조회
        List<User> existsUserList = userRepository.findByUserId(userId);

        // 2. 존재하는 회원인지 확인
        User user;
        if (existsUserList != null && existsUserList.size() > 0) {
            user = existsUserList.get(0);
            if (!user.getIsDeleted().equals("N")) {
                throw new BaseException(NOT_FOUND_USER);
            }
        } else {
            throw new BaseException(NOT_FOUND_USER);
        }

        // 3. User를 return
        return user;
    }

    /**
     * 로그인
     *
     * @param postLoginReq
     * @return PostLoginRes
     * @throws BaseException
     */
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        // 1. DB에서 id로 UserInfo 조회
        User user = retrieveUserByUserId(postLoginReq.getUserId());

        // 2. User에서 password 추출
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        // 3. 비밀번호 일치 여부 확인
        if (!postLoginReq.getPassword().equals(password)) {
            throw new BaseException(WRONG_PASSWORD);
        }

        // Create JWT
        String jwt = jwtService.createJwt(user.getUserIdx());


        // 4. PostLoginRes 변환하여 return
        Integer userIdx = user.getUserIdx();
        String userId = user.getUserId();
        String nickname = user.getNickname();
        return new PostLoginRes(userIdx, userId, nickname, jwt);
    }


    /**
     * 현재 재생 목록 조회
     *
     * @return List<GetCurrentPlaylistRes>
     * @throws BaseException
     */
    public List<GetCurrentPlaylistRes> retrieveCurrentPlaylist() throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = retrieveUserByUserIdx(userIdx);
        UserMusicPlay userMusicPlay;
        try {
            userMusicPlay = userMusicPlayRepository.findById(userIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USERMUSICPLAY);
        }

        Integer currentPlayMusicIdx = -1;
        String isShuffled = "N";
        Integer replayType = 0;
        if (userMusicPlay != null) {
            currentPlayMusicIdx = userMusicPlay.getCurrentPosition();
            isShuffled = userMusicPlay.getIsShuffled();
            replayType = userMusicPlay.getReplayType();
        }

        List<CurrentPlaylistMusic> currentPlaylistMusicList = new ArrayList<>();
        for (int i = 0; i < user.getCurrentPlaylistMusics().size(); i++) {
            if (user.getCurrentPlaylistMusics().get(i).getIsDeleted().equals("N")) {
                currentPlaylistMusicList.add(user.getCurrentPlaylistMusics().get(i));
            }
        }

        Collections.sort(currentPlaylistMusicList, new Comparator<CurrentPlaylistMusic>() {
            @Override
            public int compare(CurrentPlaylistMusic o1, CurrentPlaylistMusic o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        List<GetCurrentPlaylistRes> getCurrentPlaylistResList = new ArrayList<>();
        for (int i = 0; i < currentPlaylistMusicList.size(); i++) {
            CurrentPlaylistMusic currentPlaylistMusic = currentPlaylistMusicList.get(i);
            Integer currentPlaylistMusicIdx = currentPlaylistMusic.getCurrentPlaylistMusicIdx();
            Music music = currentPlaylistMusic.getMusic();
            Integer musicIdx = music.getMusicIdx();
            String musicTitle = music.getMusicTitle();
            String musicUrl = music.getMusicUrl();

            List<MusicSinger> singers = music.getSingers();
            List<Integer> musicSingersIdx = new ArrayList<>();
            List<String> musicSingersName = new ArrayList<>();
            for (int j = 0; j < singers.size(); j++) {
                Singer singer = singers.get(j).getSinger();
                musicSingersIdx.add(singer.getSingerIdx());
                musicSingersName.add(singer.getSingerName());
            }

            Album album = music.getAlbum();
            Integer albumIdx = album.getAlbumIdx();
            String albumImgUrl = album.getAlbumImgUrl();

            Integer order = currentPlaylistMusic.getOrder();
            String isCurrentPlayMusic = "N";
            if (currentPlaylistMusic.getCurrentPlaylistMusicIdx() == currentPlayMusicIdx) {
                isCurrentPlayMusic = "Y";
            }

            getCurrentPlaylistResList.add(new GetCurrentPlaylistRes(currentPlaylistMusicIdx, musicIdx, musicTitle, musicUrl, musicSingersIdx, musicSingersName, albumIdx, albumImgUrl, isShuffled, replayType, order, isCurrentPlayMusic));
        }

        return getCurrentPlaylistResList;
    }


    /**
     * 현재 재생 곡 조회
     *
     * @return GetCurrentPlayMusicRes
     * @throws BaseException
     */
    public GetCurrentPlayMusicRes retrieveCurrentPlayMusic() throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = retrieveUserByUserIdx(userIdx);

        UserMusicPlay userMusicPlay;
        try {
            userMusicPlay = userMusicPlayRepository.findById(userIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USERMUSICPLAY);
        }
        if (userMusicPlay == null)
            throw new BaseException(FAILED_TO_GET_USERMUSICPLAY);

        Integer currentPlaylistMusicIdx = userMusicPlay.getCurrentPosition();
        String isShuffled = userMusicPlay.getIsShuffled();
        Integer replayType = userMusicPlay.getReplayType();

        CurrentPlaylistMusic currentPlaylistMusic;
        try {
            currentPlaylistMusic = currentPlaylistMusicRepository.findById(currentPlaylistMusicIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_CURRENTPLAYMUSIC);
        }

        if (currentPlaylistMusic != null) {
            Music music = currentPlaylistMusic.getMusic();
            Integer musicIdx = music.getMusicIdx();
            String musicTitle = music.getMusicTitle();
            String musicUrl = music.getMusicUrl();
            String lyric = music.getLyric();

            List<UserVoucher> userVouchers = new ArrayList<>();
            for (int i = 0; i < user.getUserVouchers().size(); i++) {
                if (user.getUserVouchers().get(i).getVoucherEnd().equals("N")) {
                    userVouchers.add(user.getUserVouchers().get(i));
                }
            }

            boolean find = false;
            for (int i = 0; i < userVouchers.size(); i++) {
                if (userVouchers.get(i).getVoucher().getHasStreaming().equals("Y")) {
                    find = true;
                }
            }

            String hasUserVoucher;
            String musicLength;
            if (userVouchers == null || userVouchers.size() <= 0 || find == false) {
                hasUserVoucher = "N";
                musicLength = "1:00";
            } else {
                hasUserVoucher = "Y";
                //시간 mm:ss
                Time length = music.getMusicLength();
                musicLength = new SimpleDateFormat("mm:ss").format(length);
            }
            List<MusicSinger> singers = music.getSingers();
            List<Integer> musicSingersIdx = new ArrayList<>();
            List<String> musicSingersName = new ArrayList<>();
            for (int i = 0; i < singers.size(); i++) {
                Singer singer = singers.get(i).getSinger();
                musicSingersIdx.add(singer.getSingerIdx());
                musicSingersName.add(singer.getSingerName());
            }

            Album album = music.getAlbum();
            Integer albumIdx = album.getAlbumIdx();
            String albumImgUrl = album.getAlbumImgUrl();

            List<User> musicLikes = music.getMusicLikesUsers();
            Integer musicLikeCnt = 0;
            String musicLike = "N";
            for (int i = 0; i < musicLikes.size(); i++) {
                if (musicLikes.get(i).getUserIdx() == userIdx) {
                    musicLike = "Y";
                }
            }

            return new GetCurrentPlayMusicRes(currentPlaylistMusicIdx, musicIdx, musicTitle, musicUrl, musicSingersIdx, musicSingersName, albumIdx, albumImgUrl, musicLike, musicLikeCnt, lyric, musicLength, isShuffled, replayType, userIdx, hasUserVoucher);

        } else {
            throw new BaseException(NOT_FOUND_CURRENTPLAYMUSIC);
        }
    }
}
