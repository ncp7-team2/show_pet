package bitcamp.show_pet.member.model.dao;

import bitcamp.show_pet.member.model.vo.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberDao {

    int insert(Member member);

    List<Member> findAll();

    Member findBy(int memberId);

    Member findByEmailAndPassword(
            @Param("email") String email,
            @Param("password") String password);

    int update (Member member);

    int delete(int no);
}
              