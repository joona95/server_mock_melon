package com.waffle.demo.src.user;

import com.waffle.demo.src.user.models.User;
import com.waffle.demo.src.user.models.UserVoucher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface UserVoucherRepository extends CrudRepository<UserVoucher, Integer>{
    List<UserVoucher> findByVoucherEnd(String voucherEnd);
    List<UserVoucher> findByUserAndVoucherEnd(User user, String voucherEnd);
}
