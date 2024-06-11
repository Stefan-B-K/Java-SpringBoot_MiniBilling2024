package com.istef.minibilling2024.dao;

import com.istef.minibilling2024.entity.User;

import java.util.List;

public interface UserDAO {
    User findByRefNumber(String refNumber);
    List<User> findAll();
    User save(User user);
}
