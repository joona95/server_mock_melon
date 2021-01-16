package com.waffle.demo.src._test;

import org.springframework.data.jpa.repository.JpaRepository;
import com.waffle.demo.src._test.models.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
