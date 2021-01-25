package com.waffle.demo.src.singer;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.BaseResponse;
import com.waffle.demo.src.singer.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/singers")
public class SingerController {
    private final SingerProvider singerProvider;
    private final SingerService singerService;

    @Autowired
    public SingerController(SingerProvider singerProvider, SingerService singerService){
        this.singerProvider = singerProvider;
        this.singerService = singerService;
    }

    /**
     * 전체 가수 조회 API
     * [GET] /singers
     * @return BaseResponse<List<GetSingersRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetSingersRes>> getSingers(@RequestParam(value="genre", required = false) String genrePar) {
        try{
            List<GetSingersRes> getSingersResList = singerProvider.retrieveSingerList(genrePar);
            return new BaseResponse<>(SUCCESS_READ_SINGERS, getSingersResList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 상세 조회 API
     * [GET] /singers/:singerIdx
     * @PathVariable singerIdx
     * @return BaseResponse<GetSingerRes>
     */
    @ResponseBody
    @GetMapping("/{singerIdx}")
    public BaseResponse<GetSingerRes> getSinger(@PathVariable Integer singerIdx){
        if(singerIdx==null || singerIdx<=0){
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try{
            GetSingerRes getSingerRes = singerProvider.retrieveSinger(singerIdx);
            return new BaseResponse<>(SUCCESS_READ_SINGER, getSingerRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 등록 API
     * [POST] /singers
     * @RequestBody PostSingerReq
     * @return BaseResponse<PostSingerRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostSingerRes> postSinger(@RequestBody @Valid PostSingerReq parameters){
        // 1. Body Parameter Validation
        if (parameters.getSingerName() == null || parameters.getSingerName().length() == 0) {
            return new BaseResponse<>(EMPTY_SINGERNAME);
        }
        if(parameters.getChannelName()==null){
            return new BaseResponse<>(EMPTY_CHANNELNAME);
        }
        if(parameters.getProfileMusicIdx()==null){
            return new BaseResponse<>(EMPTY_PROFILEMUSICIDX);
        }
        if(parameters.getProfileImgUrl()==null){
            return new BaseResponse<>(EMPTY_PROFILEIMGURL);
        }
        if (parameters.getNationality() == null || parameters.getNationality().length() == 0) {
            return new BaseResponse<>(EMPTY_NATIONALITY);
        }
        if (parameters.getSingerType() == null || parameters.getSingerType().length() == 0) {
            return new BaseResponse<>(EMPTY_SINGERTYPE);
        }
        if (parameters.getGender() == null || parameters.getGender().length() == 0) {
            return new BaseResponse<>(EMPTY_GENDER);
        }
        if (!parameters.getGender().equals("F") && !parameters.getGender().equals("M")&&!parameters.getGender().equals("C")) {
            return new BaseResponse<>(WRONG_GENDER);
        }
        if (parameters.getGenresIdx() == null ) {
            return new BaseResponse<>(EMPTY_GENRE);
        }
        for(int i=0;i<parameters.getGenresIdx().size();i++){
            if(parameters.getGenresIdx().get(i)==null || parameters.getGenresIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GENRE);
            }
        }
        if (parameters.getAgency() == null || parameters.getAgency().length() == 0) {
            return new BaseResponse<>(EMPTY_AGENCY);
        }
        if(parameters.getCareer()==null){
            return new BaseResponse<>(EMPTY_CAREER);
        }
        if(parameters.getSingerIntroduction()==null){
            return new BaseResponse<>(EMPTY_SINGERINTRODUCTION);
        }
        if(parameters.getOfficialSite()==null){
            return new BaseResponse<>(EMPTY_OFFICIALSITE);
        }
        if(parameters.getFacebook()==null){
            return new BaseResponse<>(EMPTY_FACEBOOK);
        }
        if(parameters.getTwitter()==null){
            return new BaseResponse<>(EMPTY_TWITTER);
        }
        if(parameters.getInstagram()==null){
            return new BaseResponse<>(EMPTY_INSTAGRAM);
        }
        if(parameters.getMembersIdx()==null){
            return new BaseResponse<>(EMPTY_MEMBER);
        }
        for(int i=0;i<parameters.getMembersIdx().size();i++){
            if(parameters.getMembersIdx().get(i)==null || parameters.getMembersIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_MEMBER);
            }
        }
        if(parameters.getGroupsIdx()==null){
            return new BaseResponse<>(EMPTY_GROUP);
        }
        for(int i=0;i<parameters.getGroupsIdx().size();i++){
            if(parameters.getGroupsIdx().get(i)==null || parameters.getGroupsIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GROUP);
            }
        }

        // 2. Post Album
        try {
            PostSingerRes postSingerRes = singerService.createSinger(parameters);
            return new BaseResponse<>(SUCCESS_POST_SINGER, postSingerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 정보 수정 API
     * [PATCH] /singers/:singerIdx
     * @PathVariable singerIdx
     * @RequestBody PatchSingerReq
     * @return BaseResponse<PatchSingerRes>
     */
    @ResponseBody
    @PatchMapping("/{singerIdx}")
    public BaseResponse<PatchSingerRes> patchSinger(@PathVariable Integer singerIdx, @RequestBody @Valid PatchSingerReq parameters) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        if (parameters.getSingerName() == null || parameters.getSingerName().length() == 0) {
            return new BaseResponse<>(EMPTY_SINGERNAME);
        }
        if(parameters.getChannelName()==null){
            return new BaseResponse<>(EMPTY_CHANNELNAME);
        }
        if(parameters.getProfileMusicIdx()==null){
            return new BaseResponse<>(EMPTY_PROFILEMUSICIDX);
        }
        if(parameters.getProfileImgUrl()==null){
            return new BaseResponse<>(EMPTY_PROFILEIMGURL);
        }
        if (parameters.getNationality() == null || parameters.getNationality().length() == 0) {
            return new BaseResponse<>(EMPTY_NATIONALITY);
        }
        if (parameters.getSingerType() == null || parameters.getSingerType().length() == 0) {
            return new BaseResponse<>(EMPTY_SINGERTYPE);
        }
        if (parameters.getGender() == null || parameters.getGender().length() == 0) {
            return new BaseResponse<>(EMPTY_GENDER);
        }
        if (!parameters.getGender().equals("F") && !parameters.getGender().equals("M")&&!parameters.getGender().equals("C")) {
            return new BaseResponse<>(WRONG_GENDER);
        }
        if (parameters.getGenresIdx() == null ) {
            return new BaseResponse<>(EMPTY_GENRE);
        }
        for(int i=0;i<parameters.getGenresIdx().size();i++){
            if(parameters.getGenresIdx().get(i)==null|| parameters.getGenresIdx().get(i) <= 0){
                return new BaseResponse<>(EMPTY_GENRE);
            }
        }
        if (parameters.getAgency() == null || parameters.getAgency().length() == 0) {
            return new BaseResponse<>(EMPTY_AGENCY);
        }
        if(parameters.getCareer()==null){
            return new BaseResponse<>(EMPTY_CAREER);
        }
        if(parameters.getSingerIntroduction()==null){
            return new BaseResponse<>(EMPTY_SINGERINTRODUCTION);
        }
        if(parameters.getOfficialSite()==null){
            return new BaseResponse<>(EMPTY_OFFICIALSITE);
        }
        if(parameters.getFacebook()==null){
            return new BaseResponse<>(EMPTY_FACEBOOK);
        }
        if(parameters.getTwitter()==null){
            return new BaseResponse<>(EMPTY_TWITTER);
        }
        if(parameters.getInstagram()==null){
            return new BaseResponse<>(EMPTY_INSTAGRAM);
        }
        if(parameters.getMembersIdx()==null){
            return new BaseResponse<>(EMPTY_MEMBER);
        }
        for(int i=0;i<parameters.getMembersIdx().size();i++){
            if(parameters.getMembersIdx().get(i)==null || parameters.getMembersIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_MEMBER);
            }
        }
        if(parameters.getGroupsIdx()==null){
            return new BaseResponse<>(EMPTY_GROUP);
        }
        for(int i=0;i<parameters.getGroupsIdx().size();i++){
            if(parameters.getGroupsIdx().get(i)==null || parameters.getGroupsIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GROUP);
            }
        }

        try {
            return new BaseResponse<>(SUCCESS_PATCH_SINGER, singerService.updateSinger(singerIdx, parameters));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 삭제 API
     * [DELETE] /singers/:singerIdx
     * @PathVariable singerIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/{singerIdx}")
    public BaseResponse<Void> deleteSinger(@PathVariable Integer singerIdx) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try {
            singerService.deleteSinger(singerIdx);
            return new BaseResponse<>(SUCCESS_DELETE_SINGER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 좋아요 생성/취소 API
     * [POST] /singers/:singerIdx/like
     * @PathVariable singerIdx
     * @return BaseResponse<Void>
    */
    @ResponseBody
    @PostMapping("/{singerIdx}/like")
    public BaseResponse<Void> postSingerLike(@PathVariable Integer singerIdx) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try {
            boolean createSingerLike = singerService.createOrDeleteSingerLike(singerIdx);
            if(createSingerLike==true) {
                return new BaseResponse<>(SUCCESS_POST_SINGERLIKE);
            }
            else{
                return new BaseResponse<>(SUCCESS_DELETE_SINGERLIKE);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 채널 조회 API
     * [GET] /singers/:singerIdx/channel
     * @PathVariable singerIdx
     * @return BaseResponse<GetSingerChannelRes>
     */
    @ResponseBody
    @GetMapping("/{singerIdx}/channel")
    public BaseResponse<GetSingerChannelRes> getSingerChannel(@PathVariable Integer singerIdx){
        if(singerIdx==null || singerIdx<=0){
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try{
            GetSingerChannelRes getSingerChannelRes = singerProvider.retrieveSingerChannel(singerIdx);
            return new BaseResponse<>(SUCCESS_READ_SINGERCHANNEL, getSingerChannelRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 곡 목록 조회 API
     * [GET] /singers/:singerIdx/musics
     * @return BaseResponse<List<GetSingerMusicRes>>
     *
     */

    @ResponseBody
    @GetMapping("/{singerIdx}/musics")
    public BaseResponse<List<GetSingerMusicRes>> getSingerMusics(@PathVariable Integer singerIdx, @RequestParam(value="type",required = false) Integer typePar,@RequestParam(value="order",required = false) Integer orderPar) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try{
            List<GetSingerMusicRes> getSingerMusicResList = singerProvider.retrieveSingerMusicList(singerIdx, typePar, orderPar);
            return new BaseResponse<>(SUCCESS_READ_SINGERMUSICS, getSingerMusicResList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 앨범 목록 조회 API
     * [GET] /singers/:singerIdx/albums
     * @return BaseResponse<List<GetSingerMusicRes>>
     */

    @ResponseBody
    @GetMapping("/{singerIdx}/albums")
    public BaseResponse<List<GetSingerAlbumRes>> getSingerAlbums(@PathVariable Integer singerIdx,@RequestParam(value="type",required = false) Integer typePar,@RequestParam(value="order",required = false) Integer orderPar) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try{
            List<GetSingerAlbumRes> getSingerAlbumResList = singerProvider.retrieveSingerAlbumList(singerIdx, typePar, orderPar);
            return new BaseResponse<>(SUCCESS_READ_SINGERALBUMS, getSingerAlbumResList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 가수 댓글 목록 조회 API
     * [GET] /singers/:singerIdx/comments
     * @return BaseResponse<List<GetSingerCommentsRes>>
     */

    @ResponseBody
    @GetMapping("/{singerIdx}/comments")
    public BaseResponse<List<GetSingerCommentsRes>> getSingerComments(@PathVariable Integer singerIdx) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        try{
            List<GetSingerCommentsRes> getSingerCommentResList = singerProvider.retrieveSingerCommentList(singerIdx);
            return new BaseResponse<>(SUCCESS_READ_SINGERCOMMENT, getSingerCommentResList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 댓글 생성 API
     * [POST] /singers/:singerIdx/comments
     * @RequestBody PostSingerCommentReq
     * @return BaseResponse<PostSingerCommentRes>
     */

    @ResponseBody
    @PostMapping("/{singerIdx}/comments")
    public BaseResponse<PostSingerCommentRes> postSingerComment(@PathVariable Integer singerIdx, @RequestBody @Valid PostSingerCommentReq parameters){
        // 1. Body Parameter Validation
        if(singerIdx==null || singerIdx<=0){
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }
        if(parameters.getSingerComment()==null || parameters.getSingerComment().length()==0){
            return new BaseResponse<>(EMPTY_SINGERCOMMENT);
        }

        // 2. Post Album
        try {
            PostSingerCommentRes postSingerCommentRes = singerService.createSingerComment(singerIdx, parameters);
            return new BaseResponse<>(SUCCESS_POST_SINGERCOMMENT, postSingerCommentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 댓글/대댓글 수정 API
     * [PATCH] /singers/:singerIdx/comments/:commentIdx
     * @PathVariable singerIdx, commentIdx
     * @RequestBody PatchSingerCommentReq
     * @return BaseResponse<PatchSingerCommentRes>
     */

    @ResponseBody
    @PatchMapping("/{singerIdx}/comments/{commentIdx}")
    public BaseResponse<PatchSingerCommentRes> patchSingerComment(@PathVariable Integer singerIdx, @PathVariable Integer commentIdx, @RequestBody @Valid PatchSingerCommentReq parameters) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        if (commentIdx == null || commentIdx <= 0) {
            return new BaseResponse<>(EMPTY_COMMENTIDX);
        }

        if(parameters.getSingerComment()==null || parameters.getSingerComment().length()==0){
            return new BaseResponse<>(EMPTY_SINGERCOMMENT);
        }

        try {
            return new BaseResponse<>(SUCCESS_PATCH_SINGERCOMMENT, singerService.updateSingerComment(singerIdx, commentIdx, parameters));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 댓글/대댓글 삭제 API
     * [DELETE] /singers/:singerIdx/comments/:commentIdx
     * @PathVariable singerIdx, commentIdx
     * @return BaseResponse<Void>
     */


    @ResponseBody
    @DeleteMapping("/{singerIdx}/comments/{commentIdx}")
    public BaseResponse<Void> deleteSingerComment(@PathVariable Integer singerIdx, @PathVariable Integer commentIdx) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        if (commentIdx == null || commentIdx <= 0) {
            return new BaseResponse<>(EMPTY_COMMENTIDX);
        }

        try {
            singerService.deleteSingerComment(singerIdx, commentIdx);
            return new BaseResponse<>(SUCCESS_DELETE_SINGERCOMMENT);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 댓글 조회 API
     * [GET] /singers/:singerIdx/comments/:commentIds
     * @return BaseResponse<GetSingerCommentRes>
     */

    @ResponseBody
    @GetMapping("/{singerIdx}/comments/{commentIdx}")
    public BaseResponse<GetSingerCommentRes> getSingerComment(@PathVariable Integer singerIdx, @PathVariable Integer commentIdx) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        if (commentIdx == null || commentIdx <= 0) {
            return new BaseResponse<>(EMPTY_COMMENTIDX);
        }

        try{
            GetSingerCommentRes getSingerCommentRes = singerProvider.retrieveSingerComment(singerIdx,commentIdx);
            return new BaseResponse<>(SUCCESS_READ_SINGERCOMMENT, getSingerCommentRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 대댓글 목록 조회 API
     * [GET] /singers/:singerIdx/comments/:commentIdx/recomments
     * @return BaseResponse<List<GetSingerReCommentRes>>
     */

    @ResponseBody
    @GetMapping("/{singerIdx}/comments/{commentIdx}/recomments")
    public BaseResponse<List<GetSingerReCommentRes>> getSingerReComment(@PathVariable Integer singerIdx, @PathVariable Integer commentIdx) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }

        if (commentIdx == null || commentIdx <= 0) {
            return new BaseResponse<>(EMPTY_COMMENTIDX);
        }

        try{
            List<GetSingerReCommentRes> getSingerReCommentResList = singerProvider.retrieveSingerReCommentList(singerIdx, commentIdx);
            return new BaseResponse<>(SUCCESS_READ_SINGERRECOMMENT, getSingerReCommentResList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 대댓글 생성 API
     * [POST] /singers/:singerIdx/comments/:commentIdx
     * @RequestBody PostSingerReCommentReq
     * @return BaseResponse<PostSingerReCommentRes>
     */

    @ResponseBody
    @PostMapping("/{singerIdx}/comments/{commentIdx}")
    public BaseResponse<PostSingerReCommentRes> postSingerReComment(@PathVariable Integer singerIdx, @PathVariable Integer commentIdx, @RequestBody PostSingerReCommentReq parameters){
        // 1. Body Parameter Validation
        if(singerIdx==null || singerIdx<=0){
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }
        if(commentIdx==null || commentIdx<=0){
            return new BaseResponse<>(EMPTY_COMMENTIDX);
        }
        if(parameters.getSingerComment()==null || parameters.getSingerComment().length()==0){
            return new BaseResponse<>(EMPTY_SINGERCOMMENT);
        }


        try {
            PostSingerReCommentRes postSingerReCommentResList = singerService.createSingerReCommentList(singerIdx, commentIdx, parameters);
            return new BaseResponse<>(SUCCESS_POST_SINGERRECOMMENT, postSingerReCommentResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 가수 댓글 좋아요 생성/취소 API
     * [POST] /singers/:singerIdx/comments/:commentIdx/like
     * @PathVariable singerIdx, commentIdx
     * @return BaseResponse<Void>
     */

    @ResponseBody
    @PostMapping("/{singerIdx}/comments/{commentIdx}/like")
    public BaseResponse<Void> postSingerCommentLike(@PathVariable Integer singerIdx, @PathVariable Integer commentIdx, @RequestBody PostSingerCommentLikeReq parameters) {
        if (singerIdx == null || singerIdx <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }
        if (commentIdx == null || commentIdx <= 0) {
            return new BaseResponse<>(EMPTY_COMMENTIDX);
        }

        if(parameters.getLike()==null || parameters.getLike().length()==0){
            return new BaseResponse<>(EMPTY_LIKE);
        }
        if(!parameters.getLike().equals("L")&&!parameters.getLike().equals("H")){
            return new BaseResponse<>(WRONG_LIKE);
        }

        try {
            boolean createSingerLike = singerService.createOrDeleteSingerCommentLike(singerIdx, commentIdx, parameters);
            if(parameters.getLike().equals("L")) {
                if (createSingerLike == true) {
                    return new BaseResponse<>(SUCCESS_POST_SINGERCOMMENTLIKE);
                } else {
                    return new BaseResponse<>(SUCCESS_DELETE_SINGERCOMMENTLIKE);
                }
            }
            else{
                if (createSingerLike == true) {
                    return new BaseResponse<>(SUCCESS_POST_SINGERCOMMENTHATE);
                } else {
                    return new BaseResponse<>(SUCCESS_DELETE_SINGERCOMMENTHATE);
                }
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
