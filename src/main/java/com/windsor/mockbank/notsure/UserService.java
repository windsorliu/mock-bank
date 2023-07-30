package com.windsor.mockbank.notsure;

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

    public Integer register(UserRegisterRequest userRegisterRequest) {
        // 檢查email是否被註冊
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        if (user != null) {
            log.warn("The email: {} has been registered", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 使用MD5生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        userRegisterRequest.setPassword(hashedPassword);

        // 生成user的unique_id
        userRegisterRequest.setUserKey(UniqueIdentifierGenerator.generateUserKey());

        // 創建帳號
        return userDao.createUser(userRegisterRequest);
    }

    public User login(UserLoginRequest userLoginRequest) {
        User user = userDao.getUserByEmail(userLoginRequest.getEmail());

        // 檢查user是否存在
        if (user == null) {
            log.warn("The email: {} has not been registered yet", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 使用MD5生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        // 比較密碼
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        } else {
            log.warn("email: {} wrong password", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
