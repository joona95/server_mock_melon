package com.waffle.demo.src.voucher.models;

import lombok.*;

@Getter
@AllArgsConstructor
public class PostVoucherRes {
    private final Integer voucherIdx;
    private final String voucherName;
    private final String voucherPrice;
    private final String hasStreaming;
    private final String hasDownload;
}
