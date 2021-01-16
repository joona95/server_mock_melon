package com.waffle.demo.src.voucher.models;

import com.waffle.demo.src.user.models.UserVoucher;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.waffle.demo.config.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude={"userVouchers"})
@Data // from lombok
@ToString(exclude={"userVouchers"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Voucher") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Voucher extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "voucherIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voucherIdx;

    @Column(name = "voucherName", nullable = false, length = 45)
    private String voucherName;

    @Column(name = "voucherPrice", nullable = false, length = 5)
    private Integer voucherPrice;

    @Column(name = "hasStreaming", nullable = false, length = 1)
    private String hasStreaming;

    @Column(name = "hasDownload", nullable = false, length = 1)
    private String hasDownload;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private List<UserVoucher> userVouchers = new ArrayList<UserVoucher>();

    //@Column(name = "isDeleted", nullable = false, length = 1)
    //private String isDeleted="N";

    public Voucher(String voucherName, Integer voucherPrice, String hasStreaming, String hasDownload){
        this.voucherName = voucherName;
        this.voucherPrice = voucherPrice;
        this.hasStreaming = hasStreaming;
        this.hasDownload = hasDownload;
    }



}
