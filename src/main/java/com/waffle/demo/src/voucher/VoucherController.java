package com.waffle.demo.src.voucher;

import com.waffle.demo.config.BaseException;
import com.waffle.demo.config.BaseResponse;
import com.waffle.demo.config.secret.Secret;
import com.waffle.demo.config.utils.AES128;
import com.waffle.demo.src.user.UserProvider;
import com.waffle.demo.src.user.UserService;
import com.waffle.demo.src.user.models.*;
import com.waffle.demo.src.voucher.models.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.waffle.demo.config.BaseResponseStatus.*;
import static com.waffle.demo.config.utils.ValidationRegex.*;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {
    private final VoucherProvider voucherProvider;
    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherProvider voucherProvider, VoucherService voucherService) {
        this.voucherProvider = voucherProvider;
        this.voucherService = voucherService;
    }

    /**
     * 이용권 종류 전체 조회 API
     * [GET] /vouchers
     * @return BaseResponse<List<GetVoucherRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetVoucherRes>> getVouchers() {
        try {
            List<GetVoucherRes> getVoucherResList = voucherProvider.retrieveVoucherList();
            return new BaseResponse<>(SUCCESS_READ_VOUCHER, getVoucherResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이용권 종류 생성 API
     * [POST] /vouchers
     * @RequestBody PostVoucherReq
     * @return BaseResponse<PostVoucherRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostVoucherRes> postVoucher(@RequestBody @Valid PostVoucherReq parameters) {
        // 1. Body Parameter Validation
        if (parameters.getVoucherName() == null || parameters.getVoucherName().length() == 0) {
            return new BaseResponse<>(EMPTY_VOUCHERNAME);
        }
        if (parameters.getVoucherPrice() == null) {
            return new BaseResponse<>(EMPTY_VOUCHERPRICE);
        }
        if(parameters.getHasStreaming()==null || parameters.getHasStreaming().length()==0){
            return new BaseResponse<>(EMPTY_HASSTREAMING);
        }
        if(!parameters.getHasStreaming().equals("Y")&&!parameters.getHasStreaming().equals("N")){
            return new BaseResponse<>(WRONG_HASSTREAMING);
        }
        if(parameters.getHasDownload()==null || parameters.getHasDownload().length()==0){
            return new BaseResponse<>(EMPTY_HASDOWNLOAD);
        }
        if(!parameters.getHasDownload().equals("Y")&&!parameters.getHasDownload().equals("N")){
            return new BaseResponse<>(WRONG_HASDOWNLOAD);
        }


        // 2. Post
        try {
            PostVoucherRes postVoucherRes = voucherService.createVoucher(parameters);
            return new BaseResponse<>(SUCCESS_POST_VOUCHER, postVoucherRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이용권 종류 수정 API
     * [PATCH] /vouchers/:voucherIdx
     * @PathVariable voucherIdx
     * @RequestBody PatchVoucherReq
     * @return BaseResponse<PatchVoucherRes>
     */
    @ResponseBody
    @PatchMapping("/{voucherIdx}")
    public BaseResponse<PatchVoucherRes> patchVoucher(@PathVariable Integer voucherIdx, @RequestBody @Valid PatchVoucherReq parameters) {
        if (voucherIdx == null || voucherIdx <= 0) {
            return new BaseResponse<>(EMPTY_VOUCHERIDX);
        }

        if (parameters.getVoucherName() == null || parameters.getVoucherName().length() == 0) {
            return new BaseResponse<>(EMPTY_VOUCHERNAME);
        }
        if (parameters.getVoucherPrice() == null) {
            return new BaseResponse<>(EMPTY_VOUCHERPRICE);
        }

        try {
            return new BaseResponse<>(SUCCESS_PATCH_VOUCHER, voucherService.updateVoucher(voucherIdx, parameters));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이용권 종류 삭제 API
     * [DELETE] /vouchers/:voucherIdx
     * @PathVariable voucherIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @DeleteMapping("/{voucherIdx}")
    public BaseResponse<Void> deleteVoucher(@PathVariable Integer voucherIdx) {
        if (voucherIdx == null || voucherIdx <= 0) {
            return new BaseResponse<>(EMPTY_VOUCHERIDX);
        }


        try {
            voucherService.deleteVoucher(voucherIdx);
            return new BaseResponse<>(SUCCESS_DELETE_VOUCHER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
