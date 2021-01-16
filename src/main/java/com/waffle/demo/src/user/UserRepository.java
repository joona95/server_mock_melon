package com.waffle.demo.src.user;

import com.waffle.demo.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface UserRepository extends CrudRepository<User, Integer>{
    List<User> findByIsDeleted(String isDeleted);
    List<User> findByUserId(String userId);
    List<User> findByGenderAndIsDeleted(String gender, String isDeleted);
    List<User> findByGradeAndIsDeleted(String grade, String isDeleted);
    List<User> findByGenderAndGradeAndIsDeleted(String gender, String grade, String isDeleted);
}
