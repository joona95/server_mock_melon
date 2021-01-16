package com.waffle.demo.src.music;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.BaseResponse;
import com.waffle.demo.src.album.models.GetAlbumChannelRes;
import com.waffle.demo.src.music.models.*;
import com.waffle.demo.src.music.MusicProvider;
import com.waffle.demo.src.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/musics")
public class MusicController {
    private final MusicProvider musicProvider;
    private final MusicService musicService;

    @Autowired
    public MusicController(MusicProvider musicProvider, MusicService musicService) {
        this.musicProvider = musicProvider;
        this.musicService = musicService;
    }

    /**
     * 전체 음악 조회 API
     * [GET] /musics
     * @return BaseResponse<List<GetMusicsRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetMusicsRes>> getMusics(@RequestParam(value="genre",required = false) String genrePar) {
        try{
            List<GetMusicsRes> getMusicResList = musicProvider.retrieveMusicList(genrePar);
            return new BaseResponse<>(SUCCESS_READ_MUSICS, getMusicResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 음악 상세 조회 API
     * [GET] /musics/:musicIdx
     * @PathVariable musicIdx
     * @return BaseResponse<GetAlbumRes>

    @ResponseBody
    @GetMapping("/{musicIdx}")
    public BaseResponse<GetMusicRes> getMusic(@PathVariable Integer musicIdx){
        if(musicIdx==null || musicIdx<=0){
            return new BaseResponse<>(EMPTY_MUSICIDX);
        }

        try {
            GetMusicRes getMusicRes = musicProvider.retrieveMusic(musicIdx);
            return new BaseResponse<>(SUCCESS_READ_MUSIC, getMusicRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }*/

    /**
     * 음악 등록 API
     * [POST] /musics
     * @RequestBody PostMusicReq
     * @return BaseResponse<PostMusicRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostMusicRes> postMusic(@RequestBody @Valid PostMusicReq parameters){
        // 1. Body Parameter Validation
        if (parameters.getAlbumIdx()==null || parameters.getAlbumIdx()<=0) {
            return new BaseResponse<>(EMPTY_ALBUMIDX);
        }
        if (parameters.getMusicTitle() == null || parameters.getMusicTitle().length()==0) {
            return new BaseResponse<>(EMPTY_MUSICTITLE);
        }
        if(parameters.getIsTitle()==null || parameters.getIsTitle().length()==0){
            return new BaseResponse<>(EMPTY_ISTITLE);
        }
        if(!parameters.getIsTitle().equals("Y")&&!parameters.getIsTitle().equals("N")){
            return new BaseResponse<>(WRONG_ISTITLE);
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
            return new BaseResponse<>(NOT_ENOUGH_MUSICSINGER_INFO);
        }
        if (parameters.getWriting() == null || parameters.getWriting().length() == 0) {
            return new BaseResponse<>(EMPTY_WRITING);
        }
        if (parameters.getComposing() == null || parameters.getComposing().length() == 0) {
            return new BaseResponse<>(EMPTY_COMPOSING);
        }
        if (parameters.getArranging() == null || parameters.getArranging().length() == 0) {
            return new BaseResponse<>(EMPTY_ARRANGING);
        }

        if (parameters.getMusicLength() == null) {
            return new BaseResponse<>(EMPTY_MUSICLENGTH);
        }
        if (parameters.getMusicUrl() == null || parameters.getMusicUrl().length() == 0) {
            return new BaseResponse<>(EMPTY_MUSICURL);
        }
        if(parameters.getLyric()==null){
            return new BaseResponse<>(EMPTY_LYRIC);
        }
        if (parameters.getGenresIdx() == null ) {
            return new BaseResponse<>(EMPTY_GENRE);
        }
        for(int i=0;i<parameters.getGenresIdx().size();i++){
            if(parameters.getGenresIdx().get(i)==null || parameters.getGenresIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GENRE);
            }
        }


        // 2. Post Album
        try {
            PostMusicRes postMusicRes = musicService.createMusic(parameters);
            return new BaseResponse<>(SUCCESS_POST_MUSIC, postMusicRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 음악 정보 수정 API
     * [PATCH] /musics/:musicIdx
     * @PathVariable musicIdx
     * @RequestBody PatchMusicReq
     * @return BaseResponse<PatchMusicRes>
     */
    @ResponseBody
    @PatchMapping("/{musicIdx}")
    public BaseResponse<PatchMusicRes> patchMusic(@PathVariable Integer musicIdx, @RequestBody @Valid PatchMusicReq parameters){
        if(musicIdx==null || musicIdx <=0){
            return new BaseResponse<>(EMPTY_MUSICIDX);
        }

        if (parameters.getMusicTitle() == null || parameters.getMusicTitle().length()==0) {
            return new BaseResponse<>(EMPTY_MUSICTITLE);
        }
        if(parameters.getIsTitle()==null || parameters.getIsTitle().length()==0){
            return new BaseResponse<>(EMPTY_ISTITLE);
        }
        if(!parameters.getIsTitle().equals("Y")&&!parameters.getIsTitle().equals("N")){
            return new BaseResponse<>(WRONG_ISTITLE);
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
            return new BaseResponse<>(NOT_ENOUGH_MUSICSINGER_INFO);
        }
        if (parameters.getWriting() == null || parameters.getWriting().length() == 0) {
            return new BaseResponse<>(EMPTY_WRITING);
        }
        if (parameters.getComposing() == null || parameters.getComposing().length() == 0) {
            return new BaseResponse<>(EMPTY_COMPOSING);
        }
        if (parameters.getArranging() == null || parameters.getArranging().length() == 0) {
            return new BaseResponse<>(EMPTY_ARRANGING);
        }

        if (parameters.getMusicLength() == null) {
            return new BaseResponse<>(EMPTY_MUSICLENGTH);
        }
        if (parameters.getMusicUrl() == null || parameters.getMusicUrl().length() == 0) {
            return new BaseResponse<>(EMPTY_MUSICURL);
        }
        if(parameters.getLyric()==null){
            return new BaseResponse<>(EMPTY_LYRIC);
        }
        if (parameters.getGenresIdx() == null ) {
            return new BaseResponse<>(EMPTY_GENRE);
        }
        for(int i=0;i<parameters.getGenresIdx().size();i++){
            if(parameters.getGenresIdx().get(i)==null || parameters.getGenresIdx().get(i)<=0){
                return new BaseResponse<>(EMPTY_GENRE);
            }
        }

        try{
            return new BaseResponse<>(SUCCESS_PATCH_MUSIC, musicService.updateMusic(musicIdx, parameters));
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 음악 삭제 API
     * [DELETE] /musics/:musicIdx
     * @PathVariable musicIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/{musicIdx}")
    public BaseResponse<Void> deleteMusic(@PathVariable Integer musicIdx){
        if(musicIdx==null || musicIdx<=0){
            return new BaseResponse<>(EMPTY_MUSICIDX);
        }

        try{
            musicService.deleteMusic(musicIdx);
            return new BaseResponse<>(SUCCESS_DELETE_MUSIC);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 음악 좋아요 생성/취소 API
     * [POST] /musics/:musicIdx/like
     * @PathVariable musicIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PostMapping("/{musicIdx}/like")
    public BaseResponse<Void> postMusicLike(@PathVariable Integer musicIdx) {
        if (musicIdx == null || musicIdx <= 0) {
            return new BaseResponse<>(EMPTY_MUSICIDX);
        }

        try {
            boolean createMusicLike = musicService.createOrDeleteMusicLike(musicIdx);
            if(createMusicLike==true) {
                return new BaseResponse<>(SUCCESS_POST_MUSICLIKE);
            }
            else{
                return new BaseResponse<>(SUCCESS_DELETE_MUSICLIKE);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 음악 채널 조회 API
     * [GET] /musics/:musicIdx/channel
     * @PathVariable musicIdx
     * @return BaseResponse<GetMusicChannelRes>
     */
    @ResponseBody
    @GetMapping("/{musicIdx}/channel")
    public BaseResponse<GetMusicChannelRes> getMusicChannel(@PathVariable Integer musicIdx){
        if(musicIdx==null || musicIdx<=0){
            return new BaseResponse<>(EMPTY_MUSICIDX);
        }

        try{
            GetMusicChannelRes getMusicChannelRes = musicProvider.retrieveMusicChannel(musicIdx);
            return new BaseResponse<>(SUCCESS_READ_MUSICCHANNEL, getMusicChannelRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
