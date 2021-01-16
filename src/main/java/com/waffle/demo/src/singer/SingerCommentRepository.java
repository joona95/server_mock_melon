package com.waffle.demo.src.singer;

import com.waffle.demo.src.singer.models.Singer;
import com.waffle.demo.src.singer.models.SingerComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface SingerCommentRepository extends CrudRepository<SingerComment, Integer> {
    List<SingerComment> findBySingerCommentIdxParentAndIsDeleted(Integer singerCommentIdxParent, String isDeleted);
    List<SingerComment> findByIsDeleted(String isDeleted);
}
