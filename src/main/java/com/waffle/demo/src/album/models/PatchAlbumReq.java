package com.waffle.demo.src.album.models;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PatchAlbumReq {
    private List<@Positive Integer> singersIdx;

    private List<String> types;
    private List<String> isSingers;
    @Size(min=1, max=45)
    private String albumTitle;

    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$")
    private String releaseDate;

    private String albumImgUrl;
    @Size(min=1, max=45)
    private String releaseCompany;
    @Size(min=1, max=45)
    private String agency;
    private String albumIntroduction;
    @Size(min=1, max=20)
    private String albumType;
    private List<@Positive Integer> genresIdx;
}
