package com.waffle.demo.src._test.models;

import lombok.*;
import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
    private String name;
    private int age;

    public Member(String name, int age){
        this.name=name;
        this.age=age;
    }
}
