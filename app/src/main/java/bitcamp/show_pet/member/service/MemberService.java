package bitcamp.show_pet.member.service;

import bitcamp.show_pet.member.model.vo.Member;

public interface MemberService {
    Member get(String email, String password) throws Exception;
}
