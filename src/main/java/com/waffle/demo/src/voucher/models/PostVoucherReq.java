package com.waffle.demo.src.voucher.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PostVoucherReq {
    @Size(min=1, max=45)
    private String voucherName;
    @Positive
    private Integer voucherPrice;
    @Size(max=1)
    private String hasStreaming;
    @Size(max=1)
    private String hasDownload;
}
