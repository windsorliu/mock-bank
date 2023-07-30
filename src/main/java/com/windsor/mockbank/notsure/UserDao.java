package com.windsor.mockbank.notsure;

import com.windsor.mockbank.notsure.dto.UserRegisterRequest;
import com.windsor.mockbank.model.User;
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
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public User getUserById(Integer id) {
        String sql = "SELECT user_id, user_key, email, password, created_date, last_modified_date " +
                "FROM user WHERE user_id = :userId";

        return getUser(sql, "userId", id);
    }

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

}
