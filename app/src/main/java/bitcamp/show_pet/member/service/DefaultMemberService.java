package bitcamp.show_pet.member.service;

import bitcamp.show_pet.member.model.dao.MemberDao;
import bitcamp.show_pet.member.model.vo.Member;
import org.springframework.stereotype.Service;

@Service
public class DefaultMemberService implements MemberService {

    MemberDao memberDao;

    public DefaultMemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public Member get(String email, String password) throws Exception {
        return memberDao.findByEmailAndPassword(email, password);
    }
}
