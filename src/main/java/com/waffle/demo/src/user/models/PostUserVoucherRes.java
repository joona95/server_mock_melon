package com.waffle.demo.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class PostUserVoucherRes {
    private final Integer userVoucherIdx;
    private final String voucherName;
    private final String voucherStartDate;
    private final String voucherEndDate;
    private final String voucherStop;
    private final String voucherStopReason;
}
