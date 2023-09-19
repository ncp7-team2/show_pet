package bitcamp.show_pet.member.model.dao;

import bitcamp.show_pet.member.model.vo.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDao {

    Member findByEmailAndPassword(
            @Param("email") String email,
            @Param("password") String password);
}
              