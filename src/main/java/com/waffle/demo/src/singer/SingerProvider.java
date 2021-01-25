package com.waffle.demo.src.singer;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.utils.JwtService;
import com.waffle.demo.src.album.AlbumProvider;
import com.waffle.demo.src.album.AlbumSingerRepository;
import com.waffle.demo.src.album.models.Album;
//import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.album.models.AlbumSinger;
import com.waffle.demo.src.genre.GenreRepository;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.music.MusicProvider;
//import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.music.MusicSingerRepository;
import com.waffle.demo.src.music.models.MusicSinger;
import com.waffle.demo.src.singer.models.*;
import com.waffle.demo.src.music.models.Music;
import com.waffle.demo.src.singer.SingerRepository;
import com.waffle.demo.src.album.AlbumRepository;
import com.waffle.demo.src.music.MusicRepository;
import com.waffle.demo.src.user.CurrentPlaylistMusicRepository;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.models.CurrentPlaylistMusic;
import com.waffle.demo.src.user.models.User;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class SingerProvider {
    private final SingerRepository singerRepository;
    private final SingerCommentRepository singerCommentRepository;
    private final SingerCommentLikeRepository singerCommentLikeRepository;
    private final GenreRepository genreRepository;
    private final AlbumSingerRepository albumSingerRepository;
    private final MusicSingerRepository musicSingerRepository;
    private final CurrentPlaylistMusicRepository currentPlaylistMusicRepository;
    private final AlbumRepository albumRepository;
    private final MusicRepository musicRepository;
    private final MusicProvider musicProvider;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    @Autowired
    public SingerProvider(SingerRepository singerRepository, SingerCommentLikeRepository singerCommentLikeRepository, AlbumSingerRepository albumSingerRepository, GenreRepository genreRepository, MusicSingerRepository musicSingerRepository, SingerCommentRepository singerCommentRepository, CurrentPlaylistMusicRepository currentPlaylistMusicRepository, AlbumRepository albumRepository, MusicRepository musicRepository, MusicProvider musicProvider, UserProvider userProvider,  JwtService jwtService){
        this.singerRepository = singerRepository;
        this.albumSingerRepository = albumSingerRepository;
        this.singerCommentLikeRepository = singerCommentLikeRepository;
        this.genreRepository = genreRepository;
        this.musicSingerRepository = musicSingerRepository;
        this.singerCommentRepository = singerCommentRepository;
        this.currentPlaylistMusicRepository = currentPlaylistMusicRepository;
        this.albumRepository = albumRepository;
        this.musicRepository = musicRepository;
        this.musicProvider = musicProvider;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    /**
     * 전체 가수 조회
     * @return List<GetSingersRes>
     * @throws BaseException
     */
    public List<GetSingersRes> retrieveSingerList(String genrePar) throws BaseException {
        List<Singer> singerList;
        try{
            if(genrePar!=null){
                Genre genre = genreRepository.findByGenreAndIsDeleted(genrePar, "N");
                singerList = singerRepository.findByGenres(genre);
            }
            else {
                singerList = singerRepository.findByIsDeleted("N");
            }
        } catch (Exception ignored){
            throw new BaseException(FAILED_TO_GET_SINGER);
        }

        return singerList.stream().map(singer -> {
            Integer singerIdx = singer.getSingerIdx();
            String singerName = singer.getSingerName();
            String profileImgeUrl = singer.getProfileImgUrl();
            String nationality = singer.getNationality();
            String singerType = singer.getSingerType();

            List<Integer> genresIdx = new ArrayList<>();
            List<String> genres = new ArrayList<>();
            for(int i=0;i<singer.getGenres().size();i++) {
                Genre genre = singer.getGenres().get(i);
                genresIdx.add(genre.getGenreIdx());
                genres.add(genre.getGenre());
            }

            return new GetSingersRes(singerIdx, singerName, profileImgeUrl, nationality, singerType, genresIdx, genres);
        }).collect(Collectors.toList());
    }

    /**
     * 가수 상세 조회
     * @param singerIdx
     * @return GetSingerRes
     * @throws BaseException
     */
    public GetSingerRes retrieveSinger(Integer singerIdx) throws BaseException {
        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        String singerName = singer.getSingerName();
        String nationality = singer.getNationality();
        String singerType = singer.getSingerType();
        String gender = singer.getGender();
        String agency = singer.getAgency();
        String career = singer.getCareer();
        String singerIntroduction = singer.getSingerIntroduction();
        String officialSite = singer.getOfficialSite();
        String facebook = singer.getFacebook();
        String twitter = singer.getTwitter();
        String instagram = singer.getInstagram();

        List<Integer> genresIdx = new ArrayList<>();
        List<String> genres = new ArrayList<>();
        for(int i=0;i<singer.getGenres().size();i++) {
            Genre genre = singer.getGenres().get(i);
            genresIdx.add(genre.getGenreIdx());
            genres.add(genre.getGenre());
        }

        //데뷔 정보
        Date debutDate =null;
        Integer debutMusicIdx=-1;
        String debutMusicTitle="";
        List<String> debutMusicSingersName= new ArrayList<>();
        Album album = null;
        for(int i=0;i<singer.getAlbumSingers().size();i++) {
            if (debutDate == null) {
                album = singer.getAlbumSingers().get(i).getAlbum();
                debutDate = album.getReleaseDate();
            } else {
                int r = debutDate.compareTo(singer.getAlbumSingers().get(i).getAlbum().getReleaseDate());
                if (r < 0) {
                    album = singer.getAlbumSingers().get(i).getAlbum();
                    debutDate = album.getReleaseDate();
                }
            }
        }

        if(album!=null) {
            Music music = musicRepository.findFirstByAlbumAndIsTitleAndIsDeleted(album, "Y", "N");

            if (music != null) {
                debutMusicIdx = music.getMusicIdx();
                debutMusicTitle = music.getMusicTitle();

                for (int j = 0; j < music.getSingers().size(); j++) {
                    if (music.getSingers().get(j).getIsSinger().equals("Y") && music.getSingers().get(j).getIsDeleted().equals("N")) {
                        System.out.println(music.getSingers().get(j).getSinger().getSingerName());
                        debutMusicSingersName.add(music.getSingers().get(j).getSinger().getSingerName());
                    }
                }
            }
        }

        List<Singer> memberLists = singer.getMemberLists();
        List<Integer> membersIdx = new ArrayList<>();
        List<String> members = new ArrayList<>();
        for(int i=0;i<memberLists.size();i++){
            Singer member = memberLists.get(i);
            membersIdx.add(member.getSingerIdx());
            members.add(member.getSingerName());
        }

        List<Singer> groupLists = singer.getGroupLists();
        List<Integer> groupsIdx = new ArrayList<>();
        List<String> groupsName = new ArrayList<>();
        List<String> groupsGenres = new ArrayList<>();
        List<String> groupsLike = new ArrayList<>();
        for(int i=0;i<groupLists.size();i++){
            Singer group = groupLists.get(i);
            groupsIdx.add(group.getSingerIdx());
            groupsName.add(group.getSingerName());

            if(group.getGenres().size()>0) {
                String groupGenre = group.getGenres().get(0).getGenre();
                for (int j = 1; j < group.getGenres().size(); j++) {
                    groupGenre += (", " + group.getGenres().get(j).getGenre());
                }
                groupsGenres.add(groupGenre);
            }
            else{
                groupsGenres.add("");
            }

            String groupLike = "N";
            List<User> users = group.getSingerLikesUsers();

            for (int j = 0; j < users.size(); j++) {
                if (users.get(j).getUserIdx() == jwtService.getUserIdx()) {
                    groupLike = "Y";
                }
            }


            groupsLike.add(groupLike);

        }

        List<Singer> agencySingers;
        try {
            agencySingers = singerRepository.findByAgencyAndIsDeleted(agency, "N");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SINGER);
        }
        List<Integer> agencySingersIdx = new ArrayList<>();
        List<String> agencySingersName = new ArrayList<>();
        List<String> agencySingerGenres = new ArrayList<>();
        List<String> agencySingersLike = new ArrayList<>();
        for(int i=0;i<agencySingers.size();i++){
            Singer agencySinger = agencySingers.get(i);
            if(agencySinger.getSingerIdx()!=singerIdx) {
                agencySingersIdx.add(agencySinger.getSingerIdx());
                agencySingersName.add(agencySinger.getSingerName());

                if(agencySinger.getGenres().size()>0) {
                    String agencyGenre = agencySinger.getGenres().get(0).getGenre();
                    for (int j = 1; j < agencySinger.getGenres().size(); j++) {
                        agencyGenre += (", " + agencySinger.getGenres().get(j).getGenre());
                    }
                    agencySingerGenres.add(agencyGenre);
                }
                else{
                    agencySingerGenres.add("");
                }
                String agencyLike = "N";
                List<User> users = agencySinger.getSingerLikesUsers();

                for (int j = 0; j < users.size(); j++) {
                    if (users.get(j).getUserIdx() == jwtService.getUserIdx()) {
                        agencyLike = "Y";
                    }
                }



                agencySingersLike.add(agencyLike);

            }
        }


        return new GetSingerRes(singerIdx, singerName, nationality, singerType, gender, genresIdx, genres, agency, career, singerIntroduction, officialSite, facebook, twitter, instagram, debutMusicIdx, debutMusicTitle, debutMusicSingersName, debutDate, membersIdx, members, groupsIdx, groupsName, groupsGenres, groupsLike, agencySingersIdx, agencySingersName, agencySingerGenres, agencySingersLike);
    }

    /**
     * Idx로 가수 조회
     * @param singerIdx
     * @return Singer
     * @throws BaseException
     */
    public Singer retrieveSingerBySingerIdx(Integer singerIdx) throws BaseException {
        Singer singer;
        try{
            singer = singerRepository.findById(singerIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SINGER);
        }

        if(singer== null || !singer.getIsDeleted().equals("N")){
            throw new BaseException(NOT_FOUND_SINGER);
        }

        return singer;
    }

    /**
     * 가수 채널 조회
     * @param singerIdx
     * @return List<GetSingerChannelRes>
     * @throws BaseException
     */

    public GetSingerChannelRes retrieveSingerChannel(Integer singerIdx) throws BaseException{
        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        String singerName = singer.getSingerName();
        String channelName = singer.getChannelName();
        Integer profileMusicIdx = singer.getProfileMusicIdx();
        String profileImgUrl = singer.getProfileImgUrl();

        String profileMusicTitle="";
        List<String> profileMusicSingersName = new ArrayList<>();
        if(profileMusicIdx>0) {
            Music music = musicProvider.retrieveMusicByMusicIdx(profileMusicIdx);

            MusicSinger musicSinger = musicSingerRepository.findByMusicAndSinger(music, singer);
            if(musicSinger==null){
                throw new BaseException(FAILED_TO_GET_SINGERCHANNEL);
            }

            profileMusicTitle = music.getMusicTitle();
            for(int i=0;i<music.getSingers().size();i++){
                profileMusicSingersName.add(music.getSingers().get(i).getSinger().getSingerName());
            }
        }

        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserIdx());
        Integer userMusicLikeCnt=0;

        for(int i=0;i<user.getMusicLikes().size();i++){
            Music music = user.getMusicLikes().get(i);
            MusicSinger musicSinger = musicSingerRepository.findByMusicAndSinger(music, singer);

            if(musicSinger!=null){
                userMusicLikeCnt++;
            }
        }

        List<CurrentPlaylistMusic> playMusics = new ArrayList<>();
        try{
            playMusics = currentPlaylistMusicRepository.findByUser(user);
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_GET_CURRENTPLAYLIST);
        }

        Integer userMusicPlayCnt=0;

        for(int i=0;i<playMusics.size();i++){
            Music music = playMusics.get(i).getMusic();
            MusicSinger musicSinger = musicSingerRepository.findByMusicAndSinger(music, singer);

            if(musicSinger!=null){
                userMusicPlayCnt++;
            }
        }

        String singerLike = "N";
        Integer singerLikeCnt = singer.getSingerLikesUsers().size();
        List<User> users = singer.getSingerLikesUsers();

        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserIdx() == jwtService.getUserIdx()){
                singerLike = "Y";
            }
        }

        return new GetSingerChannelRes(singerIdx, singerName, channelName, profileMusicIdx, profileMusicTitle, profileMusicSingersName, profileImgUrl, userMusicLikeCnt, userMusicPlayCnt, singerLike, singerLikeCnt);
    }


    /**
     * 가수 곡 목록 조회
     * @param singerIdx
     * @return List<GetSingerMusicRes>
     * @throws BaseException
     */

    public List<GetSingerMusicRes> retrieveSingerMusicList(Integer singerIdx, Integer typePar, Integer orderPar) throws BaseException{
        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        List<Music> musicList= new ArrayList<>();
        List<MusicSinger> musicSingers;

        String type="";
        if(typePar!=null&&typePar==1){//참여
            type = "참여";
        }
        else if(typePar!=null && typePar==2){ //작사/작곡
            type = "작사/작곡";
        }

        try {
            if (orderPar != null && orderPar == 1) { //인기순
                if(typePar!=null && (typePar==1 || typePar==2)) {
                    musicSingers = musicSingerRepository.findByTypeAndSingerAndIsDeletedOrderByMusicLikeCnt(singer, type);
                }
                else{
                    musicSingers = musicSingerRepository.findBySingerAndIsSingerAndIsDeletedOrderByMusicLikeCnt(singer);
                }
            } else if (orderPar != null && orderPar == 2) {//가나다순
                if(typePar!=null && (typePar==1 || typePar==2)) {
                    musicSingers = musicSingerRepository.findByTypeAndSingerAndIsDeletedOrderByMusicTitle(singer, type);
                }
                else{
                    musicSingers = musicSingerRepository.findBySingerAndIsSingerAndIsDeletedOrderByMusicTitle(singer);
                }
            } else { //최신순
                if(typePar!=null && (typePar==1 || typePar==2)) {
                    musicSingers = musicSingerRepository.findByTypeAndSingerAndIsDeletedOrderByMusicCreatedAt(singer, type);
                }
                else{
                    musicSingers = musicSingerRepository.findBySingerAndIsSingerAndIsDeletedOrderByMusicCreatedAt(singer);
                }
            }
        }
        catch (Exception ignored){
            throw new BaseException(FAILED_TO_GET_MUSICSINGER);
        }

        if(musicSingers!=null) {
            for (int i = 0; i < musicSingers.size(); i++) {
                if (musicSingers.get(i).getIsDeleted().equals("N") && musicSingers.get(i).getMusic().getIsDeleted().equals("N")) {
                    musicList.add(musicSingers.get(i).getMusic());
                }
            }
        }
        else{
            throw new BaseException(FAILED_TO_GET_SINGERMUSIC);
        }
/*
        if(orderPar!=null && orderPar.equals("인기순")){

            Collections.sort(musicList, new Comparator<Music>() {
                @Override
                public int compare(Music o1, Music o2) {
                    if(o1.getMusicLikesUsers().size()<o2.getMusicLikesUsers().size()){
                        return -1;
                    }
                    else if(o1.getMusicLikesUsers().size()==o2.getMusicLikesUsers().size()){
                        return 0;
                    }
                    else{
                        return 1;
                    }

                }
            });
        }
        else if(orderPar!=null &&orderPar.equals("가나다순")) {

            Collections.sort(musicList, new Comparator<Music>() {
                @Override
                public int compare(Music o1, Music o2) {
                    return o1.getMusicTitle().compareTo(o2.getMusicTitle());
                }
            });
        }
        else {
            Collections.sort(musicList, new Comparator<Music>() {
                @Override
                public int compare(Music o1, Music o2) {
                    return o1.getAlbum().getReleaseDate().compareTo(o2.getAlbum().getReleaseDate());
                }
            });
        }
*/

        return musicList.stream().map(music -> {
            Integer musicIdx = music.getMusicIdx();
            String musicTitle = music.getMusicTitle();
            String isTitle = music.getIsTitle();

            Album album = music.getAlbum();
            Integer albumIdx = album.getAlbumIdx();
            String albumImgUrl = album.getAlbumImgUrl();

            List<Integer> musicSingersIdx = new ArrayList<>();
            List<String> musicSingersName = new ArrayList<>();
            List<MusicSinger> singers = music.getSingers();
            String musicSingerType=null;
            for(int i=0;i<singers.size();i++){
                if(singers.get(i).getIsSinger().equals("Y")&&singers.get(i).getIsDeleted().equals("N")) {
                    musicSingersIdx.add(singers.get(i).getSinger().getSingerIdx());
                    musicSingersName.add(singers.get(i).getSinger().getSingerName());

                    if(singers.get(i).getSinger().getSingerIdx()== singerIdx){
                        musicSingerType = singers.get(i).getType();
                    }
                }
            }

            return new GetSingerMusicRes(musicIdx, musicTitle, albumIdx, albumImgUrl, musicSingersIdx, musicSingersName, isTitle, musicSingerType);
        }).collect(Collectors.toList());
    }

    /**
     * 가수 앨범 목록 조회
     * @param singerIdx
     * @return List<GetSingerAlbumRes>
     * @throws BaseException
     */

    public List<GetSingerAlbumRes> retrieveSingerAlbumList(Integer singerIdx, Integer typePar, Integer orderPar) throws BaseException{
        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        List<Album> albumList= new ArrayList<>();
        List<AlbumSinger> albumSingers;
        String type="";
        if(typePar!=null&&typePar==1){//정규/싱글
            type = "정규/싱글";
        }
        else if(typePar!=null && typePar==2){ //OST/참여
            type = "OST/참여";
        }

        try {
            if (orderPar != null && orderPar == 1) { //인기순
                if(typePar!=null && (typePar==1 || typePar==2)){
                    albumSingers = albumSingerRepository.findByTypeAndSingerAndIsDeletedOrderByAlbumLikeCnt(singer, type);
                }
                else{
                    albumSingers = albumSingerRepository.findBySingerAndIsDeletedOrderByAlbumLikeCnt(singer);
                }
            } else if (orderPar != null && orderPar == 2) {//가나다순
                if(typePar!=null && (typePar==1 || typePar==2)){
                    albumSingers = albumSingerRepository.findByTypeAndSingerAndIsDeletedOrderByAlbumTitle(singer, type);
                }
                else{
                    albumSingers = albumSingerRepository.findBySingerAndIsDeletedOrderByAlbumTitle(singer);
                }
            } else { //최신순
                if(typePar!=null && (typePar==1 || typePar==2)){
                    albumSingers = albumSingerRepository.findByTypeAndSingerAndIsDeletedOrderByAlbumCreatedAt(singer, type);
                }
                else{
                    albumSingers = albumSingerRepository.findBySingerAndIsDeletedOrderByAlbumCreatedAt(singer);
                }
            }
        }
        catch (Exception ignored){
            throw new BaseException(FAILED_TO_GET_ALBUMSINGER);
        }

        if(albumSingers!=null) {
            for (int i = 0; i < albumSingers.size(); i++) {
                if (albumSingers.get(i).getIsDeleted().equals("N") && albumSingers.get(i).getAlbum().getIsDeleted().equals("N")) {
                    albumList.add(albumSingers.get(i).getAlbum());
                }
            }
        }
        else{
            throw new BaseException(FAILED_TO_GET_ALBUM);
        }

/*
        if(orderPar!=null &&orderPar.equals("인기순")){
            Collections.sort(albumList, new Comparator<Album>() {
                @Override
                public int compare(Album o1, Album o2) {
                    if(o1.getAlbumLikesUsers().size()<o2.getAlbumLikesUsers().size()){
                        return -1;
                    }
                    else if(o1.getAlbumLikesUsers().size()==o2.getAlbumLikesUsers().size()){
                        return 0;
                    }
                    else{
                        return 1;
                    }

                }
            });
        }
        else if(orderPar!=null &&orderPar.equals("가나다순")) {
            Collections.sort(albumList, new Comparator<Album>() {
                @Override
                public int compare(Album o1, Album o2) {
                    return -(o1.getAlbumTitle().compareTo(o2.getAlbumTitle()));
                }
            });
        }
        else {
            Collections.sort(albumList, new Comparator<Album>() {
                @Override
                public int compare(Album o1, Album o2) {
                    return -(o1.getReleaseDate().compareTo(o2.getReleaseDate()));
                }
            });
        }
*/

        return albumList.stream().map(album -> {
            Integer albumIdx = album.getAlbumIdx();
            String albumTitle = album.getAlbumTitle();
            String albumImgUrl = album.getAlbumImgUrl();
            Integer musicCnt = album.getMusics().size();

            List<Integer> musicsIdx = new ArrayList<>();
            for(int i=0;i<album.getMusics().size();i++){
                musicsIdx.add(album.getMusics().get(i).getMusicIdx());
            }

            List<String> albumSingersName = new ArrayList<>();
            List<AlbumSinger> singers = album.getSingers();
            String albumSingerType=null;
            for(int i=0;i<singers.size();i++){
                if(singers.get(i).getIsSinger().equals("Y")&&singers.get(i).getIsDeleted().equals("N")) {
                    albumSingersName.add(singers.get(i).getSinger().getSingerName());

                    if(singers.get(i).getSinger().getSingerIdx()== singerIdx){
                        albumSingerType = singers.get(i).getType();
                    }
                }
            }

            return new GetSingerAlbumRes(albumIdx, albumTitle, albumImgUrl, albumSingerType, albumSingersName, musicCnt, musicsIdx);
        }).collect(Collectors.toList());
    }


    /**
     * 가수 댓글 목록 조회
     * @return List<GetSingerCommentsRes>
     * @throws BaseException
     */

    public List<GetSingerCommentsRes> retrieveSingerCommentList(Integer singerIdx) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        List<SingerComment> singerCommentList = singerCommentRepository.findByIsDeleted("N");
        List<GetSingerCommentsRes> getSingerCommentResList = new ArrayList<>();
        if(singerCommentList==null)
            throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);

        for(int j=0;j<singerCommentList.size();j++) {
            if (singerCommentList.get(j).getSingerCommentIdxParent() == null || singerCommentList.get(j).getSingerCommentIdxParent() <= 0) {
                SingerComment singerComment = singerCommentList.get(j);
                Integer singerCommentIdx = singerComment.getSingerCommentIdx();
                String comment = singerComment.getSingerComment();
                Integer musicIdx = singerComment.getMusicIdx();
                String photoUrl = singerComment.getPhotoUrl();

                String musicTitle = "";
                List<String> musicSingersName = new ArrayList<>();
                if (musicIdx != null && musicIdx > 0) {
                    Music music;
                    try {
                        music = musicProvider.retrieveMusicByMusicIdx(musicIdx);
                    } catch (BaseException exception) {
                        throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
                    }
                    musicTitle = music.getMusicTitle();
                    for (int i = 0; i < music.getSingers().size(); i++) {
                        musicSingersName.add(music.getSingers().get(i).getSinger().getSingerName());
                    }
                }

                User commentUser = singerComment.getUser();
                Integer commentUserIdx = commentUser.getUserIdx();
                String nickname = commentUser.getNickname();
                String profileImgeUrl = commentUser.getUserProfileUrl();
                String isUser = "N";
                if (commentUserIdx == userIdx) {
                    isUser = "Y";
                }

                List<SingerCommentLike> singerCommentLikes = singerComment.getSingerCommentLikes();
                String singerCommentLike = "N";
                Integer singerCommentLikeCnt = 0;
                String singerCommentHate = "N";
                Integer singerCommentHateCnt = 0;
                for (int i = 0; i < singerCommentLikes.size(); i++) {
                    if(singerCommentLikes.get(i).getIsDeleted().equals("N")) {
                        if (singerCommentLikes.get(i).getLike().equals("L")) {
                            singerCommentLikeCnt++;
                            if (singerCommentLikes.get(i).getUser().getUserIdx() == userIdx) {
                                singerCommentLike = "Y";
                            }
                        } else {
                            singerCommentHateCnt++;
                            if (singerCommentLikes.get(i).getUser().getUserIdx() == userIdx) {
                                singerCommentHate = "Y";
                            }
                        }
                    }
                }

                Integer singerRecommentCnt = 0;
                List<SingerComment> singerRecomment = new ArrayList<>();
                try {
                    singerRecomment = singerCommentRepository.findBySingerCommentIdxParentAndIsDeleted(singerCommentIdx,"N");
                } catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
                }
                if (singerRecomment != null && singerRecomment.size() > 0) {
                    singerRecommentCnt = singerRecomment.size();
                }


                java.util.Date today = new java.util.Date();
                long time1 = today.getTime();
                long time2 = singerComment.getCreatedAt().getTime();
                long diff = time1 - time2;
                long min = diff / 60000;
                String commentTime;
                if (min < 60) {
                    commentTime = Long.toString(min) + "분 전";
                } else if (min < 1440) {
                    int hour = Long.valueOf(min / 60).intValue();
                    commentTime = Integer.toString(hour) + "시간 전";
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    commentTime = formatter.format(singerComment.getCreatedAt());
                }

                getSingerCommentResList.add(new GetSingerCommentsRes(singerCommentIdx, comment, musicIdx, musicTitle, musicSingersName, photoUrl, commentUserIdx, isUser, nickname, profileImgeUrl, commentTime, singerCommentLike, singerCommentLikeCnt, singerCommentHate, singerCommentHateCnt, singerRecommentCnt));

            }
        }

        return getSingerCommentResList;
    }


    /**
     * 가수 댓글 조회
     * @return GetSingerCommentRes
     * @throws BaseException
     */

    public GetSingerCommentRes retrieveSingerComment(Integer singerIdx, Integer commentIdx) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        SingerComment singerComment;
        try {
            singerComment = singerCommentRepository.findById(commentIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
        }

        if(singerComment!=null&&singerComment.getIsDeleted().equals("N")&&(singerComment.getSingerCommentIdxParent()==null || singerComment.getSingerCommentIdxParent()<=0 )) {
            Integer singerCommentIdx = singerComment.getSingerCommentIdx();
            String comment = singerComment.getSingerComment();
            Integer musicIdx = singerComment.getMusicIdx();
            String photoUrl = singerComment.getPhotoUrl();

            String musicTitle = "";
            List<String> musicSingersName = new ArrayList<>();
            if (musicIdx != null && musicIdx > 0) {
                Music music;
                try {
                    music = musicProvider.retrieveMusicByMusicIdx(musicIdx);
                } catch (BaseException exception) {
                    throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
                }
                musicTitle = music.getMusicTitle();
                for (int i = 0; i < music.getSingers().size(); i++) {
                    musicSingersName.add(music.getSingers().get(i).getSinger().getSingerName());
                }
            }

            User commentUser = singerComment.getUser();
            Integer commentUserIdx = commentUser.getUserIdx();
            String nickname = commentUser.getNickname();
            String profileImgeUrl = commentUser.getUserProfileUrl();
            String isUser = "N";
            if (commentUserIdx == userIdx) {
                isUser = "Y";
            }

            List<SingerCommentLike> singerCommentLikes = singerComment.getSingerCommentLikes();
            String singerCommentLike = "N";
            Integer singerCommentLikeCnt = 0;
            String singerCommentHate = "N";
            Integer singerCommentHateCnt = 0;
            for (int i = 0; i < singerCommentLikes.size(); i++) {
                if(singerCommentLikes.get(i).getIsDeleted().equals("N")) {
                    if (singerCommentLikes.get(i).getLike().equals("L")) {
                        singerCommentLikeCnt++;
                        if (singerCommentLikes.get(i).getUser().getUserIdx() == userIdx) {
                            singerCommentLike = "Y";
                        }
                    } else {
                        singerCommentHateCnt++;
                        if (singerCommentLikes.get(i).getUser().getUserIdx() == userIdx) {
                            singerCommentHate = "Y";
                        }
                    }
                }
            }


            java.util.Date today = new java.util.Date();
            long time1 = today.getTime();
            long time2 = singerComment.getCreatedAt().getTime();
            long diff = time1 - time2;
            long min = diff / 60000;
            String commentTime;
            if (min < 60) {
                commentTime = Long.toString(min) + "분 전";
            } else if (min < 1440) {
                int hour = Long.valueOf(min / 60).intValue();
                commentTime = Integer.toString(hour) + "시간 전";
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                commentTime = formatter.format(singerComment.getCreatedAt());
            }

            return new GetSingerCommentRes(singerCommentIdx, comment, musicIdx, musicTitle, musicSingersName, photoUrl, commentUserIdx, isUser, nickname, profileImgeUrl, commentTime, singerCommentLike, singerCommentLikeCnt, singerCommentHate, singerCommentHateCnt);
        }
        else{
            throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
        }

    }

    /**
     * 가수 대댓글 목록 조회
     * @return List<GetSingerReCommentRes>
     * @throws BaseException
     */


    public List<GetSingerReCommentRes> retrieveSingerReCommentList(Integer singerIdx, Integer commentIdx) throws BaseException {
        Integer userIdx = jwtService.getUserIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        Singer singer = retrieveSingerBySingerIdx(singerIdx);

        SingerComment parent;
        try {
            parent = singerCommentRepository.findById(commentIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SINGERCOMMENT);
        }

        List<GetSingerReCommentRes> getSingerReCommentResList = new ArrayList<>();

        if(parent!=null&&parent.getIsDeleted().equals("N")&&(parent.getSingerCommentIdxParent()==null || parent.getSingerCommentIdxParent()<=0)) {
            List<SingerComment> singerCommentList = new ArrayList<>();
            List<SingerComment> child;
            try {
                child = singerCommentRepository.findBySingerCommentIdxParentAndIsDeleted(commentIdx,"N");
            } catch (Exception ignored) {
                throw new BaseException(FAILED_TO_GET_SINGERRECOMMENT);
            }
            if (child != null || child.size() > 0) {
                singerCommentList.addAll(child);
            }

            for (int j = 0; j < singerCommentList.size(); j++) {
                SingerComment singerComment = singerCommentList.get(j);
                Integer singerCommentIdx = singerComment.getSingerCommentIdx();
                String comment = singerComment.getSingerComment();

                User commentUser = singerComment.getUser();
                Integer commentUserIdx = commentUser.getUserIdx();
                String nickname = commentUser.getNickname();
                String isUser = "N";
                if (commentUserIdx == userIdx) {
                    isUser = "Y";
                }


                java.util.Date today = new java.util.Date();
                long time1 = today.getTime();
                long time2 = singerComment.getCreatedAt().getTime();
                long diff = time1 - time2;
                long min = diff / 60000;
                String commentTime;
                if (min < 60) {
                    commentTime = Long.toString(min) + "분 전";
                } else if (min < 1440) {
                    int hour = Long.valueOf(min / 60).intValue();
                    commentTime = Integer.toString(hour) + "시간 전";
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    commentTime = formatter.format(singerComment.getCreatedAt());
                }

                getSingerReCommentResList.add(new GetSingerReCommentRes(singerCommentIdx, comment, commentUserIdx, isUser, nickname, commentTime));
            }
            return getSingerReCommentResList;
        }
        else{
            throw new BaseException(FAILED_TO_GET_SINGERRECOMMENT);
        }


    }


}
