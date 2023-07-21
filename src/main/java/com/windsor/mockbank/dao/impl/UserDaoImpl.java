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
        String sql = "SELECT id, unique_id, email, password, created_date, last_modified_date " +
                "FROM user WHERE id = :id";

        return getUser(sql, "id", id);
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT id, unique_id, email, password, created_date, last_modified_date " +
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
        String sql = "INSERT INTO user(unique_id, email, password) VALUES (:uniqueId, :email, :password)";

        Map<String, Object> map = new HashMap<>();
        map.put("uniqueId", userRegisterRequest.getUniqueId());
        map.put("email", userRegisterRequest.getEmail());
        map.put("password", userRegisterRequest.getPassword());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        Integer id = keyHolder.getKey().intValue();

        return id;
    }
}
