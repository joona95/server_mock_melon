package com.waffle.demo.src.genre;

import com.waffle.demo.src.genre.models.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface GenreRepository extends CrudRepository<Genre, Integer>{
    Genre findByGenreAndIsDeleted(String genre, String isDeleted);
}
