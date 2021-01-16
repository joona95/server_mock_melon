package com.waffle.demo.src.album.models;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostAlbumRateReq {
    @DecimalMin(value = "0.0")
    @Digits(integer = 1, fraction = 1)
    private Float rate;
}
