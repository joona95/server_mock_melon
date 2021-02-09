package com.waffle.demo.src._test.models;

import lombok.*;
import javax.persistence.*;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="member")
public class Member {
    @EmbeddedId
    private MemberPK pk;

    @Column(name="age")
    private int age;

    public Member(MemberPK pk, int age){
        this.pk = pk;
        this.age=age;
    }
}
