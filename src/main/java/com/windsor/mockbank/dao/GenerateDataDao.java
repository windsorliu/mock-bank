package com.windsor.mockbank.dao;

import com.windsor.mockbank.dto.UserRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class GenerateDataDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final static Logger log = LoggerFactory.getLogger(GenerateDataDao.class);

    public void createUserData(List<UserRegisterRequest> userRegisterRequestList) {
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

        log.info("Successfully generated {} records of users data.", userRegisterRequestList.size());
    }
}
