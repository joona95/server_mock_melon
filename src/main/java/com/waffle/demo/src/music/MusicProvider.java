package com.waffle.demo.src.music;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.utils.JwtService;
//import com.waffle.demo.src.album.models.AlbumLike;
import com.waffle.demo.src.album.models.AlbumRate;
import com.waffle.demo.src.album.models.GetAlbumChannelRes;
import com.waffle.demo.src.genre.GenreRepository;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.music.models.*;
import com.waffle.demo.src.album.models.Album;
import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.music.MusicRepository;
import com.waffle.demo.src.user.CurrentPlayMusicRepository;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.models.CurrentPlayMusic;
import com.waffle.demo.src.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class MusicProvider {
    private final MusicRepository musicRepository;
    private final UserProvider userProvider;
    private final CurrentPlayMusicRepository currentPlayMusicRepository;
    private final Chart100Repository chart100Repository;
    private final GenreRepository genreRepository;
    private final JwtService jwtService;

    @Autowired
    public MusicProvider(MusicRepository musicRepository, CurrentPlayMusicRepository currentPlayMusicRepository, UserProvider userProvider, Chart100Repository chart100Repository, GenreRepository genreRepository, JwtService jwtService){
        this.musicRepository = musicRepository;
        this.currentPlayMusicRepository = currentPlayMusicRepository;
        this.userProvider = userProvider;
        this.chart100Repository = chart100Repository;
        this.genreRepository = genreRepository;
        this.jwtService = jwtService;
    }

    /**
     * 전체 음악 조회
     * @return List<GetMusicRes>
     * @throws BaseException
     */
    public List<GetMusicsRes> retrieveMusicList(String genrePar) throws BaseException {
        List<Music> musicList;
        try{
            if(genrePar!=null){
                Genre genre = genreRepository.findByGenreAndIsDeleted(genrePar, "N");
                musicList = musicRepository.findByGenres(genre);
            }
            else {
                musicList = musicRepository.findByIsDeleted("N");
            }
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MUSIC);
        }

        return musicList.stream().map(music -> {
            int musicIdx = music.getMusicIdx();
            //albumIdx 구하기
            Album album = music.getAlbum();
            Integer albumIdx = album.getAlbumIdx();
            String albumImgUrl = album.getAlbumImgUrl();

            String musicTitle = music.getMusicTitle();
            String isTitle = music.getIsTitle();

            //시간 mm:ss
            //Time length = music.getMusicLength();
            //String musicLength = new SimpleDateFormat("mm:ss").format(length);

            //가수
            List<MusicSinger> musicsingers = music.getSingers();
            List<Integer> singersIdx = new ArrayList<>();
            List<String> singersName = new ArrayList<>();
            for(int i=0;i<musicsingers.size();i++){
                if(music.getSingers().get(i).getIsSinger().equals("Y")&&music.getSingers().get(i).getIsDeleted().equals("N")) {
                    Singer singer = musicsingers.get(i).getSinger();
                    singersIdx.add(singer.getSingerIdx());
                    singersName.add(singer.getSingerName());
                }
            }

            //장르정보
            List<Integer> genresIdx = new ArrayList<>();
            List<String> genres = new ArrayList<>();
            for(int i=0;i<music.getGenres().size();i++){
                Genre genre = music.getGenres().get(i);
                genresIdx.add(genre.getGenreIdx());
                genres.add(genre.getGenre());
            }

            return new GetMusicsRes(musicIdx, albumIdx, albumImgUrl, musicTitle, isTitle, singersIdx, singersName, genresIdx, genres);
        }).collect(Collectors.toList());
    }

    /**
     * 음악 상세 조회
     * @param musicIdx
     * @return GetMusicRes
     * @throws BaseException
     */

    public GetMusicRes retrieveMusic(Integer musicIdx) throws BaseException {

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());

        Music music = retrieveMusicByMusicIdx(musicIdx);

        //albumIdx 구하기
        Album album = music.getAlbum();
        Integer albumIdx = album.getAlbumIdx();

        String musicTitle = music.getMusicTitle();
        String isTitle = music.getIsTitle();
        String writing = music.getWriting();
        String composing = music.getComposing();
        String arranging = music.getArranging();

        //가수
        List<Integer> singersIdx = new ArrayList<>();
        List<String> singersName = new ArrayList<>();
        for(int i=0;i<music.getSingers().size();i++){
            if(music.getSingers().get(i).getIsSinger().equals("Y")&&music.getSingers().get(i).getIsDeleted().equals("N")) {
                Singer singer = music.getSingers().get(i).getSinger();
                singersIdx.add(singer.getSingerIdx());
                singersName.add(singer.getSingerName());
            }
        }

        //장르정보
        List<Integer> genresIdx = new ArrayList<>();
        List<String> genres = new ArrayList<>();
        for(int i=0;i<music.getGenres().size();i++){
            Genre genre = music.getGenres().get(i);
            genresIdx.add(genre.getGenreIdx());
            genres.add(genre.getGenre());
        }

        Integer yesterdayChartRank = -1;
        try{
            Chart100 yesterdayChart = chart100Repository.findByMusicAndIsDeleted(music, "N");
            if(yesterdayChart!=null){
                yesterdayChartRank = yesterdayChart.getRank();
            }
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_CHART100);
        }

        Integer bestChartRank = -1;
        String bestChartRankDate = "";
        try{
            Chart100 bestChart = chart100Repository.findFirstByMusicOrderByRankDesc(music);
            if(bestChart!=null){
                bestChartRank = bestChart.getRank();

                SimpleDateFormat datetime = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                bestChartRankDate = datetime.format(bestChart.getCreatedAt());
            }
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_CHART100);
        }

        Integer firstRankCnt = 0;
        List<Chart100> firstRankCharts = new ArrayList<>();
        try{
            firstRankCharts.addAll(chart100Repository.findByMusicAndRankOrderByCreatedAt(music, 1));
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_CHART100);
        }
        firstRankCnt = firstRankCharts.size();

        Integer yesterdayUserPlayCnt = 0;
        String yesterdayMaleUserPlayPercent = "0%";
        String yesterdayFemaleUserPlayPercent = "0%";

        java.util.Date date = new java.util.Date();
        date = new Date(date.getTime()-(1000*60*60*24));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dDate = sdf.format(date);
        Timestamp yesterday = Timestamp.valueOf(dDate);
        List<CurrentPlayMusic> yesterdayPlayMusics = new ArrayList<>();
        try{
            yesterdayPlayMusics.addAll(currentPlayMusicRepository.findByYesterdayUserPlayMusic(yesterday, music));
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_CURRENTPLAYMUSIC);
        }
        if(yesterdayPlayMusics.size()>0){
            yesterdayUserPlayCnt = yesterdayPlayMusics.size();
            int m=0;
            for(int i=0;i<yesterdayPlayMusics.size();i++){
                if(yesterdayPlayMusics.get(i).getUser().getGender().equals("M")){
                    m++;
                }
            }

            float mp = m/yesterdayUserPlayCnt;
            DecimalFormat df = new DecimalFormat("0%");
            yesterdayMaleUserPlayPercent = df.format(m);
            yesterdayFemaleUserPlayPercent = df.format(1-m);
        }


        String userFirstPlayDate = "";
        Integer userPlayCnt = 0;
        List<CurrentPlayMusic> currentPlayMusics=new ArrayList<>();
        try{
            currentPlayMusics.addAll(currentPlayMusicRepository.findByUserAndMusicAndIsDeletedOrderByCreatedAt(user, music, "N"));
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_CHART100);
        }
        if(currentPlayMusics.size()>0) {
            SimpleDateFormat datetime = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            userFirstPlayDate = datetime.format(currentPlayMusics.get(0).getCreatedAt());
            userPlayCnt = currentPlayMusics.size();
        }

        return new GetMusicRes(musicIdx, albumIdx, musicTitle, isTitle, singersIdx, singersName, writing, composing, arranging, genresIdx, genres, yesterdayChartRank, bestChartRank, bestChartRankDate, firstRankCnt, yesterdayUserPlayCnt, yesterdayMaleUserPlayPercent, yesterdayFemaleUserPlayPercent, userFirstPlayDate, userPlayCnt);
    }

    /**
     * Idx로 음악 조회
     * @param musicIdx
     * @return Music
     * @throws BaseException
     */
    public Music retrieveMusicByMusicIdx(Integer musicIdx) throws BaseException {
        Music music;
        try{
            music = musicRepository.findById(musicIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MUSIC);
        }

        if(music==null || !music.getIsDeleted().equals("N")){
            throw new BaseException(NOT_FOUND_MUSIC);
        }

        return music;
    }


    /**
     * 음악 채널 조회
     * @param musicIdx
     * @return List<GetMusicChannelRes>
     * @throws BaseException
     */

    public GetMusicChannelRes retrieveMusicChannel(Integer musicIdx) throws BaseException{

        Music music = retrieveMusicByMusicIdx(musicIdx);


        //앨범 주인인 가수들 찾아서 RES
        List<Integer> isSingersIdx=new ArrayList<>();
        List<String> isSingersName =new ArrayList<>();
        for(int i=0;i<music.getSingers().size();i++){
            if(music.getSingers().get(i).getIsSinger().equals("Y")&&music.getSingers().get(i).getIsDeleted().equals("N")) {
                Singer singer = music.getSingers().get(i).getSinger();
                isSingersIdx.add(singer.getSingerIdx());
                isSingersName.add(singer.getSingerName());
            }
        }

        List<Genre> genreList = music.getGenres();
        List<Integer> genresIdx = new ArrayList<>();
        List<String> genres = new ArrayList<>();
        for(int i=0;i<genreList.size();i++){
            Genre genre = genreList.get(i);
            genresIdx.add(genre.getGenreIdx());
            genres.add(genre.getGenre());
        }


        Integer albumIdx = music.getAlbum().getAlbumIdx();
        String albumTitle = music.getAlbum().getAlbumTitle();
        String musicTitle = music.getMusicTitle();

        //가수 프로필 사진
        String singerProfileUrl = "Various Artists";
        if(isSingersIdx.size()==1){
            Singer singer = music.getSingers().get(0).getSinger();
            singerProfileUrl = singer.getProfileImgUrl();
        }


        String musicLike = "N";
        Integer musicLikeCnt = music.getMusicLikesUsers().size();
        List<User> users = music.getMusicLikesUsers();

        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserIdx() == jwtService.getUserIdx()){
                musicLike = "Y";
            }
        }

        return new GetMusicChannelRes(musicIdx, musicTitle, albumIdx, albumTitle, genresIdx, genres, musicLike, musicLikeCnt, isSingersIdx, isSingersName, singerProfileUrl);

    }


    /**
     * 차트 순위 조회
     * @return List<GetChart100Res>
     * @throws BaseException
     */

    public List<GetChart100Res> retrieveChart100() throws BaseException{

        List<Chart100> chart100s = new ArrayList<>();
        try{
            chart100s.addAll(chart100Repository.findByIsDeleted("N"));
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_CHART100);
        }


        List<GetChart100Res> getChart100ResList = new ArrayList<>();
        for(int i=0;i<chart100s.size();i++){
            Music music = chart100s.get(i).getMusic();
            Integer musicIdx = music.getMusicIdx();
            String musicTitle = music.getMusicTitle();
            String musicUrl = music.getMusicUrl();

            Integer albumIdx = music.getAlbum().getAlbumIdx();
            String albumImgUrl = music.getAlbum().getAlbumImgUrl();


            //음악 주인인 가수들 찾아서 RES
            List<Integer> isSingersIdx=new ArrayList<>();
            List<String> isSingersName =new ArrayList<>();
            for(int j=0;j<music.getSingers().size();j++){
                if(music.getSingers().get(j).getIsSinger().equals("Y")&&music.getSingers().get(j).getIsDeleted().equals("N")) {
                    Singer singer = music.getSingers().get(j).getSinger();
                    isSingersIdx.add(singer.getSingerIdx());
                    isSingersName.add(singer.getSingerName());
                }
            }

            Integer rank = chart100s.get(i).getRank();

            getChart100ResList.add(new GetChart100Res(musicIdx, musicTitle, musicUrl, albumIdx, albumImgUrl, isSingersIdx, isSingersName, rank));
        }

        return getChart100ResList;
    }
}
