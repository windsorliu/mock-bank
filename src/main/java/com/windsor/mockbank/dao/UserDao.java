package com.windsor.mockbank.dao;

import com.windsor.mockbank.dto.UserRegisterRequest;
import com.windsor.mockbank.model.User;

public interface UserDao {

    User getUserById(Integer id);

    User getUserByEmail(String email);

    Integer createUser(UserRegisterRequest userRegisterRequest);
}
