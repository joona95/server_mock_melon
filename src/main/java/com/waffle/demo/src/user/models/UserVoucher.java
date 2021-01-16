package com.waffle.demo.src.user.models;

import com.waffle.demo.src.voucher.models.Voucher;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false, exclude = {"user", "voucher"})
@Data // from lombok
@ToString(exclude = {"user", "voucher"})
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "UserVoucher") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class UserVoucher {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "userVoucherIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userVoucherIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherIdx", nullable = false)
    private Voucher voucher;

    @CreationTimestamp
    @Column(name = "voucherStartDate", nullable = false, updatable = false)
    private Timestamp voucherStartDate;

    @Column(name = "voucherStop", nullable = false, length=1)
    private String voucherStop="N";

    @Column(name = "voucherStopReason", length=45)
    private String voucherStopReason;

    @Column(name = "voucherEnd", nullable = false, length=1)
    private String voucherEnd="N";

    public UserVoucher(User user, Voucher voucher){
        this.user = user;
        this.voucher = voucher;
    }

}
