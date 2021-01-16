package com.waffle.demo.src.singer;

import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.singer.models.SingerComment;
import com.waffle.demo.src.singer.models.SingerCommentLike;
import com.waffle.demo.src.user.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface SingerCommentLikeRepository extends CrudRepository<SingerCommentLike, Integer> {
    SingerCommentLike findByUserAndSingerComment(User user, SingerComment singerComment);
}
