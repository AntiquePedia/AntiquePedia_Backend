package com.example.antiquepedia_backend.service;

import com.example.antiquepedia_backend.Entity.User;
import com.example.antiquepedia_backend.info.LoginResponse;
import com.example.antiquepedia_backend.info.UserCollections;

public interface UserService {
    // 登录
    public LoginResponse login(User user);

    // 注册
    public String register(User user);

    // 获取某个用户所有收藏
    public UserCollections getCollectionsByUser(Integer userId);
}
