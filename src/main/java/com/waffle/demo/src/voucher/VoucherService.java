package com.waffle.demo.src.voucher;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.BaseResponse;
import com.waffle.demo.config.secret.Secret;
import com.waffle.demo.config.utils.AES128;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.UserRepository;
import com.waffle.demo.src.user.models.*;
import com.waffle.demo.src.voucher.models.*;
import com.waffle.demo.config.utils.JwtService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;

import static com.waffle.demo.config.BaseResponseStatus.*;
import static com.waffle.demo.config.utils.ValidationRegex.isRegexEmail;
import static com.waffle.demo.config.utils.ValidationRegex.isRegexPhoneNum;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherProvider voucherProvider;
    private final JwtService jwtService;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository, VoucherProvider voucherProvider, JwtService jwtService) {
        this.voucherRepository = voucherRepository;
        this.voucherProvider = voucherProvider;
        this.jwtService = jwtService;
    }

    /**
     * 이용권 종류 생성
     * @param postVoucherReq
     * @return PostVoucherRes
     * @throws BaseException
     */
    public PostVoucherRes createVoucher(PostVoucherReq postVoucherReq) throws BaseException {
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        String voucherName = postVoucherReq.getVoucherName();
        Integer voucherPrice = postVoucherReq.getVoucherPrice();
        String hasStreaming = postVoucherReq.getHasStreaming();
        String hasDownload = postVoucherReq.getHasDownload();

        Voucher voucher = new Voucher(voucherName, voucherPrice,hasStreaming, hasDownload);

        try {
            voucher = voucherRepository.save(voucher);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_VOUCHER);
        }

        Integer voucherIdx = voucher.getVoucherIdx();
        DecimalFormat formatter = new DecimalFormat("###,###");
        String price = formatter.format(voucherPrice);

        return new PostVoucherRes(voucherIdx, voucherName, price, hasStreaming, hasDownload);
    }



    /**
     * 이용권 종류 수정 (POST uri 가 겹쳤을때의 예시 용도)
     * @param patchVoucherReq
     * @return PatchVoucherRes
     * @throws BaseException
     */
    public PatchVoucherRes updateVoucher(@NonNull Integer voucherIdx, PatchVoucherReq patchVoucherReq) throws BaseException {
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Voucher voucher = voucherProvider.retrieveVoucherByVoucherIdx(voucherIdx);

        //정보 수정
        voucher.setVoucherName(patchVoucherReq.getVoucherName());
        voucher.setVoucherPrice(patchVoucherReq.getVoucherPrice());
        try {
            voucher = voucherRepository.save(voucher);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_VOUCHER);
        }

        try {
            String voucherName = voucher.getVoucherName();
            Integer voucherPrice = voucher.getVoucherPrice();
            DecimalFormat formatter = new DecimalFormat("###,###");
            String price = formatter.format(voucherPrice);
            String hasSteaming = voucher.getHasStreaming();
            String hasDownload = voucher.getHasDownload();

            return new PatchVoucherRes(voucherIdx, voucherName, price, hasSteaming, hasDownload);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_PATCH_VOUCHER);
        }
    }

    /**
     * 이용권 종류 삭제
     * @param voucherIdx
     * @throws BaseException
     */
    public void deleteVoucher(Integer voucherIdx) throws BaseException {
        if(jwtService.getUserIdx()!=1){
            throw new BaseException(FAILED_TO_ACCESS);
        }

        Voucher voucher = voucherProvider.retrieveVoucherByVoucherIdx(voucherIdx);

        voucher.setIsDeleted("Y");
        try {
            voucherRepository.save(voucher);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_DELETE_VOUCHER);
        }
    }
}
