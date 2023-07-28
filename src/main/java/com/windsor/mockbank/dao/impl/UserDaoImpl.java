package com.windsor.mockbank.dao.impl;

import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.dto.UserRegisterRequest;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.rowmapper.UserRowMapper;
import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User getUserById(Integer id) {
        String sql = "SELECT user_id, user_key, email, password, created_date, last_modified_date " +
                "FROM user WHERE user_id = :userId";

        return getUser(sql, "userId", id);
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, user_key, email, password, created_date, last_modified_date " +
                "FROM user WHERE email = :email";

        return getUser(sql, "email", email);
    }

    private User getUser(String sql, String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.isEmpty()) {
            return null;
        }

        return userList.get(0);
    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO user(user_key, email, password) VALUES (:userKey, :email, :password)";

        Map<String, Object> map = new HashMap<>();
        map.put("userKey", userRegisterRequest.getUserKey());
        map.put("email", userRegisterRequest.getEmail());
        map.put("password", userRegisterRequest.getPassword());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        Integer id = keyHolder.getKey().intValue();

        return id;
    }

    @Override
    public String generateUserData(List<UserRegisterRequest> userRegisterRequestList) {
        String sql = "INSERT INTO user(user_key, email, password) VALUE (:userKey, :email, :password)";

        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[userRegisterRequestList.size()];

        for (int i = 0; i < userRegisterRequestList.size(); i++) {
            UserRegisterRequest userRegisterRequest = userRegisterRequestList.get(i);

            parameterSources[i] = new MapSqlParameterSource();
            parameterSources[i].addValue("userKey", userRegisterRequest.getUserKey());
            parameterSources[i].addValue("email", userRegisterRequest.getEmail());
            parameterSources[i].addValue("password", userRegisterRequest.getPassword());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);

        return "BATCH INSERT SUCCESS";
    }


}
