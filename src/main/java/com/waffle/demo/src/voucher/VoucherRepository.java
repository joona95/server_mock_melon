package com.waffle.demo.src.voucher;

import com.waffle.demo.src.user.models.User;
import com.waffle.demo.src.voucher.models.Voucher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface VoucherRepository extends CrudRepository<Voucher, Integer> {
    List<Voucher> findByIsDeleted(String isDeleted);
}
