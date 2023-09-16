package bitcamp.show_pet.user.model.dao;

import bitcamp.show_pet.user.model.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    User findByEmailAndPassword(
            @Param("email") String email,
            @Param("password") String password);
}
              