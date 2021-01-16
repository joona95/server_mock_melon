package com.waffle.demo.src.user;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.BaseResponse;
import com.waffle.demo.src.user.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;
import static com.waffle.demo.config.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserProvider userProvider;
    private final UserService userService;

    @Autowired
    public UserController(UserProvider userProvider, UserService userService) {
        this.userProvider = userProvider;
        this.userService = userService;
    }

    /**
     * 회원 전체 조회 API
     * [GET] /users
     * @return BaseResponse<List<GetUserRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(value="id",required = false) String id,@RequestParam(value="gender",required = false) String genderPar, @RequestParam(value = "grade", required = false) String gradePar) {
        try {
            List<GetUserRes> getUserResList = userProvider.retrieveUserList(id, genderPar, gradePar);
            return new BaseResponse<>(SUCCESS_READ_USERS, getUserResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 조회 API
     * [GET] /users/:userIdx
     * @PathVariable userIdx
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUser(@PathVariable Integer userIdx) {
        if (userIdx == null || userIdx <= 0) {
            return new BaseResponse<>(EMPTY_USERIDX);
        }

        try {
            GetUserRes getUserRes = userProvider.retrieveUser(userIdx);
            return new BaseResponse<>(SUCCESS_READ_USER, getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
     * [POST] /users/login
     * @RequestBody PostLoginReq
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody @Valid PostLoginReq parameters) {
        // 1. Body Parameter Validation
        if (parameters.getUserId() == null || parameters.getUserId().length() == 0) {
            return new BaseResponse<>(EMPTY_ID);
        } else if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_PASSWORD);
        }

        // 2. Login
        try {
            PostLoginRes postLoginRes = userProvider.login(parameters);
            return new BaseResponse<>(SUCCESS_LOGIN, postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @RequestBody PostUserReq
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> postUser(@RequestBody @Valid PostUserReq parameters) {
        // 1. Body Parameter Validation
        if (parameters.getUserId() == null || parameters.getUserId().length() == 0) {
            return new BaseResponse<>(EMPTY_ID);
        }
        if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
        if (parameters.getConfirmPassword() == null || parameters.getConfirmPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_CONFIRM_PASSWORD);
        }
        if (!parameters.getPassword().equals(parameters.getConfirmPassword())) {
            return new BaseResponse<>(DO_NOT_MATCH_PASSWORD);
        }
        if (parameters.getNickname() == null || parameters.getNickname().length() == 0) {
            return new BaseResponse<>(EMPTY_NICKNAME);
        }
        if (parameters.getPhoneNum() == null || parameters.getPhoneNum().length() == 0) {
            return new BaseResponse<>(EMPTY_PHONENUM);
        }
        if(!isRegexPhoneNum(parameters.getPhoneNum())){
            return new BaseResponse<>(INVALID_PHONENUM);
        }
        if (parameters.getEmail() == null || parameters.getEmail().length() == 0) {
            return new BaseResponse<>(EMPTY_EMAIL);
        }
        if(!isRegexEmail(parameters.getEmail())){
            return new BaseResponse<>(INVALID_EMAIL);
        }
        if (parameters.getGender() == null || parameters.getGender().length() == 0) {
            return new BaseResponse<>(EMPTY_GENDER);
        }
        if (!parameters.getGender().equals("F") && !parameters.getGender().equals("M")) {
            return new BaseResponse<>(WRONG_GENDER);
        }

        // 2. Post UserInfo
        try {
            PostUserRes postUserRes = userService.createUser(parameters);
            return new BaseResponse<>(SUCCESS_POST_USER, postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 정보 수정 API
     * [PATCH] /users/:userIdx
     * @PathVariable userIdx
     * @RequestBody PatchUserReq
     * @return BaseResponse<PatchUserRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<PatchUserRes> patchUser(@PathVariable Integer userIdx, @RequestBody @Valid PatchUserReq parameters) {
        if (userIdx == null || userIdx <= 0) {
            return new BaseResponse<>(EMPTY_USERIDX);
        }

        if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
        if (parameters.getNickname() == null || parameters.getNickname().length() == 0) {
            return new BaseResponse<>(EMPTY_NICKNAME);
        }
        if (parameters.getPhoneNum() == null || parameters.getPhoneNum().length() == 0) {
            return new BaseResponse<>(EMPTY_PHONENUM);
        }
        if(!isRegexPhoneNum(parameters.getPhoneNum())){
            return new BaseResponse<>(INVALID_PHONENUM);
        }
        if (parameters.getEmail() == null || parameters.getEmail().length() == 0) {
            return new BaseResponse<>(EMPTY_EMAIL);
        }
        if(!isRegexEmail(parameters.getEmail())){
            return new BaseResponse<>(INVALID_EMAIL);
        }
        if(parameters.getAuthentication()==null || parameters.getAuthentication().length()==0){
            return new BaseResponse<>(EMPTY_AUTHENTICATION);
        }
        if(!parameters.getAuthentication().equals("N")&&!parameters.getAuthentication().equals("Y")){
            return new BaseResponse<>(WRONG_AUTHENTICATION);
        }

        try {
            return new BaseResponse<>(SUCCESS_PATCH_USER, userService.updateUser(userIdx, parameters));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 탈퇴 API
     * [DELETE] /users/:userIdx
     * @PathVariable userIdx
     * @RequestBody DeleteUserReq
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}")
    public BaseResponse<Void> deleteUser(@PathVariable Integer userIdx, @RequestBody @Valid DeleteUserReq parameters) {
        if (userIdx == null || userIdx <= 0) {
            return new BaseResponse<>(EMPTY_USERIDX);
        }

        if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_PASSWORD);
        }

        try {
            userService.deleteUser(userIdx, parameters);
            return new BaseResponse<>(SUCCESS_DELETE_USER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 이용권 생성 API
     * [POST] /users/vouchers
     * @RequestBody PostUserVoucherReq
     * @return BaseResponse<PostUserVoucherRes>
     */
    @ResponseBody
    @PostMapping("/vouchers")
    public BaseResponse<PostUserVoucherRes> postUserVoucher(@RequestBody @Valid PostUserVoucherReq parameters) {
        // 1. Body Parameter Validation
        if (parameters.getVoucherIdx() == null ) {
            return new BaseResponse<>(EMPTY_VOUCHERIDX);
        }

        // 2. Post UserInfo
        try {
            PostUserVoucherRes postUserVoucherRes = userService.createUserVoucher(parameters);
            return new BaseResponse<>(SUCCESS_POST_USERVOUCHER, postUserVoucherRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 이용권 삭제 API
     * [DELETE] /users/vouchers/:userVoucherIdx
     * @PathVariable userVoucherIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/vouchers/{userVoucherIdx}")
    public BaseResponse<Void> deleteUserVoucher(@PathVariable Integer userVoucherIdx) {
        if (userVoucherIdx == null || userVoucherIdx <= 0) {
            return new BaseResponse<>(EMPTY_USERVOUCHERIDX);
        }

        try {
            userService.deleteUserVoucher(userVoucherIdx);
            return new BaseResponse<>(SUCCESS_DELETE_USERVOUCHER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 현재 재생 목록 조회 API
     * [GET] /users/currentplaylist
     * @return BaseResponse<List<GetCurrentPlaylistRes>>
     */
     @ResponseBody
     @GetMapping("/currentplaylist")
     public BaseResponse<List<GetCurrentPlaylistRes>> getCurrentPlaylist() {
     try {
     List<GetCurrentPlaylistRes> getCurrentPlaylistResList = userProvider.retrieveCurrentPlaylist();
     return new BaseResponse<>(SUCCESS_READ_CURRENTPLAYLIST, getCurrentPlaylistResList);
     } catch (BaseException exception) {
     return new BaseResponse<>(exception.getStatus());
     }
     }

    /**
     * 현재 재생 목록 곡 생성 API
     * [POST] /users/currentplaylist
     * @RequestBody PostCurrentPlaylistReq
     * @return BaseResponse<Void>
     */
     @ResponseBody
     @PostMapping("/currentplaylist")
     public BaseResponse<Void> postCurrentPlaylist(@RequestBody @Valid PostCurrentPlaylistReq parameters) {
         // 1. Body Parameter Validation
         if (parameters.getMusicsIdx() == null || parameters.getMusicsIdx().size() <= 0) {
            return new BaseResponse<>(EMPTY_MUSICIDX);
         }
         if(parameters.getCreateType()==null){
             return new BaseResponse<>(EMPTY_CREATETYPE);
         }
         if(parameters.getCreateType()!=0&&parameters.getCreateType()!=1&&parameters.getCreateType()!=2){
             return new BaseResponse<>(WRONG_CREATETYPE);
         }

         // 2. Post UserInfo
         try {
            userService.createCurrentPlaylist(parameters);
            return new BaseResponse<>(SUCCESS_POST_CURRENTPLAYLIST);
         } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
         }
     }

    /**
     * 현재 재생 목록 곡 순서 변경 API
     * [POST] /users/currentplaylist
     * @RequestBody PatchCurrentPlaylistReq
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PatchMapping("/currentplaylist")
    public BaseResponse<Void> patchCurrentPlaylist(@RequestBody @Valid PatchCurrentPlaylistReq parameters) {
        // 1. Body Parameter Validation
        if (parameters.getCurrentPlaylistMusicsIdx() == null || parameters.getCurrentPlaylistMusicsIdx().size() <= 0) {
            return new BaseResponse<>(EMPTY_CURRENTPLAYLISTMUSICIDX);
        }


        try {
            userService.updateCurrentPlaylist(parameters);
            return new BaseResponse<>(SUCCESS_PATCH_CURRENTPLAYLIST);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 현재 재생 목록 곡 삭제 API
     * [DELETE] /users/currentplaylist
     * @RequestBody DeleteCurrentPlaylistReq
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/currentplaylist")
    public BaseResponse<Void> deleteCurrentPlaylist(@RequestBody @Valid DeleteCurrentPlaylistReq parameters) {
        if(parameters.getCurrentPlaylistMusicsIdx()==null || parameters.getCurrentPlaylistMusicsIdx().size()<=0){
            return new BaseResponse<>(EMPTY_CURRENTPLAYLISTMUSICIDX);
        }

        try {
            userService.deleteCurrentPlaylist(parameters);
            return new BaseResponse<>(SUCCESS_DELETE_CURRENTPLAYLIST);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 현재 재생 곡 조회 API
     * [GET] /users/currentplaymusic
     * @return BaseResponse<GetCurrentPlayMusicRes>
     */
    @ResponseBody
    @GetMapping("/currentplaymusic")
    public BaseResponse<GetCurrentPlayMusicRes> getCurrentPlayMusic() {
        try {
            GetCurrentPlayMusicRes getCurrentPlayMusicRes = userProvider.retrieveCurrentPlayMusic();
            return new BaseResponse<>(SUCCESS_READ_CURRENTPLAYMUSIC, getCurrentPlayMusicRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 현재 재생 곡 생성 API
     * [POST] /users/currentplaymusic
     * @RequestBody PostCurrentPlayMusicReq
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PostMapping("/currentplaymusic")
    public BaseResponse<Void> postCurrentPlayMusic(@RequestBody @Valid PostCurrentPlayMusicReq parameters) {
        // 1. Body Parameter Validation
        if (parameters.getCurrentPlaylistMusicIdx() == null || parameters.getCurrentPlaylistMusicIdx() <= 0) {
            return new BaseResponse<>(EMPTY_MUSICIDX);
        }

        // 2. Post UserInfo
        try {
            userService.createCurrentPlayMusic(parameters);
            return new BaseResponse<>(SUCCESS_POST_CURRENTPLAYMUSIC);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
