package bitcamp.show_pet.user.service;

import bitcamp.show_pet.user.model.vo.User;

public interface UserService {
    User get(String email, String password) throws Exception;
}
