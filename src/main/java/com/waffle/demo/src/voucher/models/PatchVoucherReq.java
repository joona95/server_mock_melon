package com.waffle.demo.src.voucher.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PatchVoucherReq {
    @Size(min=1, max=45)
    private String voucherName;
    @Positive
    private Integer voucherPrice;
}
