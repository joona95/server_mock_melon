package com.waffle.demo.src._test.models;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class MemberPK implements Serializable {

    @Column(name="id", nullable = false)
    private long id;

    @Column(name="name",nullable = false)
    private String name;

    public MemberPK(){

    }

    public MemberPK(long id, String name){
        super();
        this.id = id;
        this.name = name;
    }

}
