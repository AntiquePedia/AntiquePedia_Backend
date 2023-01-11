package com.example.antiquepedia_backend.dao.impl;


import com.example.antiquepedia_backend.Entity.User;
import com.example.antiquepedia_backend.dao.UserDao;
import com.example.antiquepedia_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(Integer id) {
        return userRepository.getUserById(id);
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.getUserByName(name);
    }

    @Override
    public void register(User user) {
        userRepository.save(user);
    }
}

