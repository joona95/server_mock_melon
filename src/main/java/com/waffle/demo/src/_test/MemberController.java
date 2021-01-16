package com.waffle.demo.src._test;

import org.springframework.web.bind.annotation.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Map;

@RequestMapping("/member")
@RestController
public class MemberController {
/*
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/test")
    public Member memberTest(){
        return new Member(0L, "juna052", 20);
    }

    @GetMapping("/test2")
    public ArrayList<Member> memberTest2(){
        return new ArrayList(Arrays.asList(
                new Member(1L, "juna1", 20),
                new Member(2L, "juna2", 30),
                new Member(3L, "juna3", 40),
                new Member(4L, "juna4", 50),
                new Member(5L, "juna5", 60)
        ));
    }

    @PostMapping("/insert") // CREATE
    public Member insert(@RequestBody Map<String, String> map){
        return memberRepository.save(
                new Member(map.get("name"), Integer.parseInt(map.get("age")))
        );
    }

    @GetMapping("/select") // READ
    public List<Member> selectAll(){
        return memberRepository.findAll();
    }

    @GetMapping("/select/{id}") // READ
    public Member selectOne(@PathVariable("id") long id){
        return memberRepository.findById(id).orElse(null);
    }

    @PostMapping("/delete/{id}") // DELETE
    public String deleteOne(@PathVariable("id") long id){
        memberRepository.deleteById(id);
        return "삭제 완료";
    }

 */
}
