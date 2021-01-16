package com.waffle.demo.src.voucher;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.secret.Secret;
import com.waffle.demo.config.utils.AES128;
import com.waffle.demo.config.utils.JwtService;
import com.waffle.demo.src.user.UserRepository;
import com.waffle.demo.src.user.models.GetUserRes;
import com.waffle.demo.src.user.models.User;
import com.waffle.demo.src.voucher.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.waffle.demo.config.BaseResponseStatus.*;

@Service
public class VoucherProvider {
    private final VoucherRepository voucherRepository;

    @Autowired
    public VoucherProvider(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    /**
     * 전체 이용권 조회
     * @return List<GetVoucherRes>
     * @throws BaseException
     */
    public List<GetVoucherRes> retrieveVoucherList() throws BaseException{

        // 1. DB에서 전체 이용권 조회
        List<Voucher> voucherList;
        try {
            voucherList = voucherRepository.findByIsDeleted("N");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_VOUCHER);
        }

        // 2. GetVoucherRes로 변환하여 return
        return voucherList.stream().map(voucher -> {
            Integer voucherIdx = voucher.getVoucherIdx();
            String voucherName = voucher.getVoucherName();
            Integer voucherPrice = voucher.getVoucherPrice();
            DecimalFormat formatter = new DecimalFormat("###,###");
            String price = formatter.format(voucherPrice);
            String hasStreaming = voucher.getHasStreaming();
            String hasDownload = voucher.getHasDownload();
            return new GetVoucherRes(voucherIdx, voucherName, price, hasStreaming, hasDownload);
        }).collect(Collectors.toList());
    }

    /**
     * Idx로 이용권 조회
     * @param voucherIdx
     * @return Voucher
     * @throws BaseException
     */
    public Voucher retrieveVoucherByVoucherIdx(Integer voucherIdx) throws BaseException {
        // 1. DB에서 Voucher 조회
        Voucher voucher;
        try {
            voucher = voucherRepository.findById(voucherIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_VOUCHER);
        }

        // 2. 존재하는 회원인지 확인
        if (voucher == null || !voucher.getIsDeleted().equals("N")) {
            throw new BaseException(NOT_FOUND_VOUCHER);
        }

        // 3. Voucher를 return
        return voucher;
    }
}
