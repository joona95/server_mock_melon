package com.waffle.demo.src.user.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostUserVoucherReq {
    @Positive
    private Integer voucherIdx;
}
