package com.windsor.mockbank.service;


import com.windsor.mockbank.dto.UserLoginRequest;
import com.windsor.mockbank.dto.UserRegisterRequest;
import com.windsor.mockbank.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Integer id);

    Integer register(UserRegisterRequest userRegisterRequest);

    User login(UserLoginRequest userLoginRequest);

    String generateUserData(List<UserRegisterRequest> userRegisterRequestList);
}
