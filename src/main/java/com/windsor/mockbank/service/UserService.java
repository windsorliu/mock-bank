package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.dto.UserRequest;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.JwtTokenGenerator;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    public User getUserById(Integer id) {
        return userDao.getUserById(id);
    }

    public Integer signUp(UserRequest userRequest) {
        // 檢查email是否被註冊
        User userCheck = userDao.getUserByEmail(userRequest.getEmail());

        if (userCheck != null) {
            log.warn("The email: {} has been registered", userRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        String userKey = UniqueIdentifierGenerator.generateUserKey();

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setUserKey(userKey);
        user.setToken(JwtTokenGenerator.generateJwtToken(userKey));

        // 創建帳號
        return userDao.createUser(user);
    }

    public User login(UserRequest userRequest) {
        User userCheck = userDao.getUserByEmail(userRequest.getEmail());

        // 檢查user是否存在
        if (userCheck == null) {
            log.warn("The email: {} has not been registered yet", userRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 比較密碼
        if (userRequest.getPassword().equals(userCheck.getPassword())) {
            return userCheck;
        } else {
            log.warn("email: {} wrong password", userRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
