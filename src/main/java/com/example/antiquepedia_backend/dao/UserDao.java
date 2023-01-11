package com.example.antiquepedia_backend.dao;

import com.example.antiquepedia_backend.Entity.User;

public interface UserDao {
    User getUserById(Integer id);

    User getUserByName(String name);

    void register(User user);
}

