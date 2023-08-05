package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.notsure.dto.UserLoginRequest;
import com.windsor.mockbank.notsure.dto.UserRegisterRequest;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    public User getUserById(Integer id) {
        return userDao.getUserById(id);
    }

    public Integer register(User user) {
        // 檢查email是否被註冊
        User userCheck = userDao.getUserByEmail(user.getEmail());

        if (userCheck != null) {
            log.warn("The email: {} has been registered", user.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 創建帳號
        return userDao.createUser(user);
    }

}
