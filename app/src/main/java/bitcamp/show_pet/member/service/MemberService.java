package bitcamp.show_pet.member.service;

import bitcamp.show_pet.member.model.vo.Member;

import java.util.List;

public interface MemberService {

    int add(Member member) throws Exception;
    List<Member> list() throws Exception;
    Member get(int memberId) throws Exception;
    Member get(String email, String password) throws Exception;
    int update(Member member) throws Exception;
    int delete(int memberId) throws Exception;

}
