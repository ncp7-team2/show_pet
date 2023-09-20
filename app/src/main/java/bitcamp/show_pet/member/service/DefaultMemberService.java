package bitcamp.show_pet.member.service;

import bitcamp.show_pet.member.model.dao.MemberDao;
import bitcamp.show_pet.member.model.vo.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultMemberService implements MemberService {

    MemberDao memberDao;

    public DefaultMemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Transactional
    @Override
    public int add(Member member) throws Exception {
        return memberDao.insert(member);
    }

    @Override
    public List<Member> list() throws Exception {
        return memberDao.findAll();
    }

    @Override
    public Member get(int memberId) throws Exception {
        return memberDao.findBy(memberId);
    }

    @Override
    public Member get(String email, String password) throws Exception {
        return memberDao.findByEmailAndPassword(email, password);
    }

    @Transactional
    @Override
    public int update(Member member) throws Exception {
        return memberDao.update(member);
    }

    @Transactional
    @Override
    public int delete(int memberId) throws Exception {
        return memberDao.delete(memberId);
    }



}
