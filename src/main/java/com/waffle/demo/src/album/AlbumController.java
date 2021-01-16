package com.waffle.demo.src.album;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.BaseResponse;
import com.waffle.demo.src.album.models.*;
import com.waffle.demo.src.album.AlbumProvider;
import com.waffle.demo.src.album.AlbumService;
import com.waffle.demo.src.singer.models.GetSingerChannelRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/albums")
public class AlbumController {
    private final AlbumProvider albumProvider;
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumProvider albumProvider, AlbumService albumService) {
        this.albumProvider = albumProvider;
        this.albumService = albumService;
    }

    /**
     * 전체 앨범 조회 API
     * [GET] /albums
     * @return BaseResponse<List<GetAlbumsRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetAlbumsRes>> getAlbums(@RequestParam(value="genre", required = false) String genrePar) {
        try{
            List<GetAlbumsRes> getAlbumResList = albumProvider.retrieveAlbumList(genrePar);
            return new BaseResponse<>(SUCCESS_READ_ALBUMS, getAlbumResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 상세 조회 API
     * [GET] /albums/:albumIdx
     * @PathVariable albumIdx
     * @return BaseResponse<GetAlbumRes>
     */
    @ResponseBody
    @GetMapping("/{albumIdx}")
    public BaseResponse<GetAlbumRes> getAlbum(@PathVariable Integer albumIdx){
        if(albumIdx==null || albumIdx <=0){
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }

        try{
            GetAlbumRes getAlbumRes = albumProvider.retrieveAlbum(albumIdx);
            return new BaseResponse<>(SUCCESS_READ_ALBUM, getAlbumRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 등록 API
     * [POST] /albums
     * @RequestBody PostAlbumReq
     * @return BaseResponse<PostAlbumRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostAlbumRes> postAlbum(@RequestBody @Valid PostAlbumReq parameters){
        // 1. Body Parameter Validation
        if (parameters.getSingersIdx() == null || parameters.getSingersIdx().size() <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }
        for(int i=0;i<parameters.getSingersIdx().size();i++){
            if(parameters.getSingersIdx().get(i)==null || parameters.getSingersIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_SINGERIDX);
            }
        }
        if (parameters.getTypes() == null || parameters.getTypes().size() <= 0) {
            return new BaseResponse<>(EMPTY_TYPE);
        }
        for(int i=0;i<parameters.getTypes().size();i++){
            if(parameters.getTypes().get(i)==null || parameters.getTypes().get(i).length()==0){
                return new BaseResponse<>(EMPTY_TYPE);
            }
        }
        if (parameters.getIsSingers() == null || parameters.getIsSingers().size() <= 0) {
            return new BaseResponse<>(EMPTY_ISSINGER);
        }
        for(int i=0;i<parameters.getIsSingers().size();i++){
            if(!parameters.getIsSingers().get(i).equals("Y")&&!parameters.getIsSingers().get(i).equals("N")){
                return new BaseResponse<>(WRONG_ISSINGER);
            }
        }
        if(parameters.getSingersIdx().size()!=parameters.getTypes().size()||parameters.getSingersIdx().size()!=parameters.getIsSingers().size()||parameters.getIsSingers().size()!=parameters.getTypes().size()){
            return new BaseResponse<>(NOT_ENOUGH_ALBUMSINGER_INFO);
        }
        if (parameters.getAlbumTitle() == null || parameters.getAlbumTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_ALBUMTITLE);
        }
        if (parameters.getReleaseDate() == null) {
            return new BaseResponse<>(EMPTY_RELEASEDATE);
        }
        if (parameters.getAlbumImgUrl() == null) {
            return new BaseResponse<>(EMPTY_ALBUMIMGURL);
        }
        if (parameters.getReleaseCompany() == null || parameters.getReleaseCompany().length() == 0) {
            return new BaseResponse<>(EMPTY_RELEASECOMPANY);
        }
        if (parameters.getAgency() == null || parameters.getAgency().length() == 0) {
            return new BaseResponse<>(EMPTY_AGENCY);
        }
        if (parameters.getAlbumIntroduction() == null) {
            return new BaseResponse<>(EMPTY_ALBUMINTRODUCTION);
        }
        if (parameters.getAlbumType() == null || parameters.getAlbumType().length() == 0) {
            return new BaseResponse<>(EMPTY_ALBUMTYPE);
        }
        if (parameters.getGenresIdx() == null || parameters.getGenresIdx().size() <= 0) {
            return new BaseResponse<>(EMPTY_GENRE);
        }
        for(int i=0;i<parameters.getGenresIdx().size();i++){
            if(parameters.getGenresIdx().get(i)==null || parameters.getGenresIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GENRE);
            }
        }

        // 2. Post Album
        try {
            PostAlbumRes postAlbumRes = albumService.createAlbum(parameters);
            return new BaseResponse<>(SUCCESS_POST_ALBUM, postAlbumRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 정보 수정 API
     * [PATCH] /albums/:albumIdx
     * @PathVariable albumIdx
     * @RequestBody PatchAlbumReq
     * @return BaseResponse<PatchAlbumRes>
     */
    @ResponseBody
    @PatchMapping("/{albumIdx}")
    public BaseResponse<PatchAlbumRes> patchAlbum(@PathVariable Integer albumIdx, @RequestBody @Valid PatchAlbumReq parameters){
        if(albumIdx==null || albumIdx<=0){
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }

        if (parameters.getSingersIdx() == null || parameters.getSingersIdx().size() <= 0) {
            return new BaseResponse<>(EMPTY_SINGERIDX);
        }
        for(int i=0;i<parameters.getSingersIdx().size();i++){
            if(parameters.getSingersIdx().get(i)==null || parameters.getSingersIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_SINGERIDX);
            }
        }
        if (parameters.getTypes() == null || parameters.getTypes().size() <= 0) {
            return new BaseResponse<>(EMPTY_TYPE);
        }
        for(int i=0;i<parameters.getTypes().size();i++){
            if(parameters.getTypes().get(i)==null || parameters.getTypes().get(i).length()==0){
                return new BaseResponse<>(EMPTY_TYPE);
            }
        }
        if (parameters.getIsSingers() == null || parameters.getIsSingers().size() <= 0) {
            return new BaseResponse<>(EMPTY_ISSINGER);
        }
        for(int i=0;i<parameters.getIsSingers().size();i++){
            if(!parameters.getIsSingers().get(i).equals("Y")&&!parameters.getIsSingers().get(i).equals("N")){
                return new BaseResponse<>(WRONG_ISSINGER);
            }
        }
        if(parameters.getSingersIdx().size()!=parameters.getTypes().size()||parameters.getSingersIdx().size()!=parameters.getIsSingers().size()||parameters.getIsSingers().size()!=parameters.getTypes().size()){
            return new BaseResponse<>(NOT_ENOUGH_ALBUMSINGER_INFO);
        }
        if (parameters.getAlbumTitle() == null || parameters.getAlbumTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_ALBUMTITLE);
        }
        if (parameters.getReleaseDate() == null) {
            return new BaseResponse<>(EMPTY_RELEASEDATE);
        }
        if (parameters.getAlbumImgUrl() == null) {
            return new BaseResponse<>(EMPTY_ALBUMIMGURL);
        }
        if (parameters.getReleaseCompany() == null || parameters.getReleaseCompany().length() == 0) {
            return new BaseResponse<>(EMPTY_RELEASECOMPANY);
        }
        if (parameters.getAgency() == null || parameters.getAgency().length() == 0) {
            return new BaseResponse<>(EMPTY_AGENCY);
        }
        if (parameters.getAlbumIntroduction() == null) {
            return new BaseResponse<>(EMPTY_ALBUMINTRODUCTION);
        }
        if (parameters.getAlbumType() == null || parameters.getAlbumType().length() == 0) {
            return new BaseResponse<>(EMPTY_ALBUMTYPE);
        }
        if (parameters.getGenresIdx() == null || parameters.getGenresIdx().size() <= 0) {
            return new BaseResponse<>(EMPTY_GENRE);
        }
        for(int i=0;i<parameters.getGenresIdx().size();i++){
            if(parameters.getGenresIdx().get(i)==null || parameters.getGenresIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GENRE);
            }
        }

        try{
            return new BaseResponse<>(SUCCESS_PATCH_ALBUM, albumService.updateAlbum(albumIdx, parameters));
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 삭제 API
     * [DELETE] /albums/:albumIdx
     * @PathVariable albumIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/{albumIdx}")
    public BaseResponse<Void> deleteAlbum(@PathVariable Integer albumIdx){
        if(albumIdx==null || albumIdx<=0){
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }

        try{
            albumService.deleteAlbum(albumIdx);
            return new BaseResponse<>(SUCCESS_DELETE_ALBUM);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 좋아요 생성/취소 API
     * [POST] /albums/:albumIdx/like
     * @PathVariable albumIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PostMapping("/{albumIdx}/like")
    public BaseResponse<Void> postAlbumLike(@PathVariable Integer albumIdx) {
        if (albumIdx == null || albumIdx <= 0) {
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }

        try {
            boolean createAlbumLike = albumService.createOrDeleteAlbumLike(albumIdx);
            if(createAlbumLike==true) {
                return new BaseResponse<>(SUCCESS_POST_ALBUMLIKE);
            }
            else{
                return new BaseResponse<>(SUCCESS_DELETE_ALBUMLIKE);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 평점 생성 API
     * [POST] /albums/:albumIdx/rate
     * @PathVariable albumIdx
     * @RequestBody PostAlbumRateReq
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PostMapping("/{albumIdx}/rate")
    public BaseResponse<Void> postAlbumRate(@PathVariable Integer albumIdx, @RequestBody @Valid PostAlbumRateReq parameters) {
        if (albumIdx == null || albumIdx <= 0) {
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }

        if (parameters.getRate() == null) {
            return new BaseResponse<>(EMPTY_ALBUMRATE);
        }

        try {
            albumService.createAlbumRate(albumIdx, parameters);
            return new BaseResponse<>(SUCCESS_POST_ALBUMRATE);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 앨범 채널 조회 API
     * [GET] /albums/:albumIdx/channel
     * @PathVariable albumIdx
     * @return BaseResponse<GetAlbumChannelRes>
     */
    @ResponseBody
    @GetMapping("/{albumIdx}/channel")
    public BaseResponse<GetAlbumChannelRes> getAlbumChannel(@PathVariable Integer albumIdx){
        if(albumIdx==null || albumIdx<=0){
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }

        try{
            GetAlbumChannelRes getAlbumChannelRes = albumProvider.retrieveAlbumChannel(albumIdx);
            return new BaseResponse<>(SUCCESS_READ_ALBUMCHANNEL, getAlbumChannelRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
