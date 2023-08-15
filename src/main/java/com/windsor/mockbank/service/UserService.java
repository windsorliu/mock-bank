package com.windsor.mockbank.service;

import com.windsor.mockbank.config.JwtService;
import com.windsor.mockbank.constant.Role;
import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.dto.UserRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final static Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final AccountDao accountDao;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setUserKey(userKey);
        user.setRole(Role.USER);
        user.setToken(jwtService.generateToken(user));

        // 創建帳號
        return userDao.createUser(user);
    }

    public User login(UserRequest userRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.getEmail(),
                        userRequest.getPassword()
                )
        );

        User user = userDao.getUserByEmail(userRequest.getEmail());
        String token = jwtService.generateToken(user);
        userDao.updateToken(user.getUserId(), token);
        User userResponse = userDao.getUserById(user.getUserId());
        return userResponse;

//        fixme
//        User userCheck = userDao.getUserByEmail(userRequest.getEmail());
//
//        // 檢查user是否存在
//        if (userCheck == null) {
//            log.warn("The email: {} has not been registered yet", userRequest.getEmail());
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//
//        String passwordCheck = passwordEncoder.encode(userRequest.getPassword());
//
//        // 比較密碼
//        if (!passwordCheck.equals(userCheck.getPassword())) {
//            log.warn("The email: {} wrong password", userRequest.getEmail());
//            System.out.println(passwordCheck);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
//
//        String token = jwtService.generateToken(userCheck);
//        userDao.updateToken(userCheck.getUserId(), token);
//        User userResponse = userDao.getUserById(userCheck.getUserId());
//        return userResponse;
    }

    public List<Account> getAccountsByUserKey(String userKey) {
        User userCheck = userDao.getUserByKey(userKey);

        // 檢查user是否存在
        if (userCheck == null) {
            log.warn("The user: {} does not exist", userKey);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Account> accountList = accountDao.getAccountsByUserId(userCheck.getUserId());

        return accountList;
    }
}
