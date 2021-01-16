package com.waffle.demo.src.genre;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.src.album.AlbumRepository;
import com.waffle.demo.src.genre.models.Genre;
import com.waffle.demo.src.album.models.*;
import com.waffle.demo.src.singer.models.Singer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class GenreProvider {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreProvider(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Idx로 장르 조회
     * @param genreIdx
     * @return Genre
     * @throws BaseException
     */
    public Genre retrieveGenreByGenreIdx(Integer genreIdx) throws BaseException{
        Genre genre;
        try{
            genre = genreRepository.findById(genreIdx).orElse(null);
        } catch (Exception ignored){
            throw new BaseException(FAILED_TO_GET_GENRE);
        }

        if(genre==null || !genre.getIsDeleted().equals("N")){
            throw new BaseException(NOT_FOUND_GENRE);
        }

        return genre;
    }

}
