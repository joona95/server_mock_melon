package com.waffle.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;


@Getter
@Setter
@MappedSuperclass //부모 클래스를 상속받는 자식클래스에게 매핑 정보가 제공하고 싶을 때.
public class BaseEntity {
    @CreationTimestamp
    @Column(name = "createAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updateAt", nullable = false)
    private Timestamp updatedAt;

    @Column(name="isDeleted", nullable = false, length=1)
    private String isDeleted = "N";


}
