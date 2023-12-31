package com.windsor.mockbank.dao;

import com.windsor.mockbank.model.User;
import com.windsor.mockbank.rowmapper.UserRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final static Logger log = LoggerFactory.getLogger(UserDao.class);

    public User getUserById(Integer id) {
        String sql = "SELECT user_id, user_key, token, email, password, created_date, last_modified_date " +
                "FROM user WHERE user_id = :userId";

        return getUser(sql, "userId", id);
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, user_key, token, email, password, created_date, last_modified_date " +
                "FROM user WHERE email = :email";

        return getUser(sql, "email", email);
    }

    public User getUserByKey(String userKey) {
        String sql = "SELECT user_id, user_key, token, email, password, created_date, last_modified_date " +
                "FROM user WHERE user_key = :user_key";

        return getUser(sql, "user_key", userKey);
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

    public Integer createUser(User user) {
        String sql = "INSERT INTO user(user_key, token, email, password) VALUES (:user_key, :token, :email, :password)";

        Map<String, Object> map = new HashMap<>();
        map.put("user_key", user.getUserKey());
        map.put("token", user.getToken());
        map.put("email", user.getEmail());
        map.put("password", user.getPassword());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        Integer id = keyHolder.getKey().intValue();

        log.info("Creating user: {}", user.getUserKey());

        return id;
    }

    public void updateToken(Integer userId, String token) {
        String sql = "UPDATE user SET token = :token WHERE user_id = :user_id";

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("token", token);

        namedParameterJdbcTemplate.update(sql, map);
    }
}
