package bitcamp.show_pet.user.service;

import bitcamp.show_pet.user.model.dao.UserDao;
import bitcamp.show_pet.user.model.vo.User;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService {

    UserDao userDao;

    @Override
    public User get(String email, String password) throws Exception {
        return userDao.findByEmailAndPassword(email, password);
    }
}
