package com.example.antiquepedia_backend.service.impl;

import com.example.antiquepedia_backend.Entity.Collection;
import com.example.antiquepedia_backend.Entity.ManMadeObject;
import com.example.antiquepedia_backend.Entity.User;
import com.example.antiquepedia_backend.dao.UserDao;
import com.example.antiquepedia_backend.info.LoginResponse;
import com.example.antiquepedia_backend.info.UserCollections;
import com.example.antiquepedia_backend.service.ManMadeObjectService;
import com.example.antiquepedia_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    ManMadeObjectService manMadeObjectService;

    @Override
    public LoginResponse login(User user) {
        LoginResponse retResp = new LoginResponse();
        String message = "Success";
        Integer userId = -1;
        Integer role = 0;
        try{
            // 这个时候只能 获得 用户名 并没有 用户id
            String userName = user.getName();
            System.out.println("here userName: " + userName);
            String password = user.getPassword();
            User user_db = userDao.getUserByName(userName);
            if (user_db == null){
                message = "User Not Existed";
            }else{
                String password_expected = user_db.getPassword();
                if(!password.equals(password_expected))
                    message = "Password Wrong";
                else {
                    userId = user_db.getId();
                    role = user_db.getRole();
                }
            }
        }catch (Exception e){
            message = "Exception";
        }
        retResp.setMessage(message);
        retResp.setUserId(userId);
        retResp.setRole(role);
        return retResp;
    }

    @Override
    public String register(User user) {
        String retStr = "Success";
        try{
            userDao.register(user);
        }catch (Exception e){
            retStr = "Exception";
        }

        return retStr;
    }

    @Override
    public UserCollections getCollectionsByUser(Integer userId) {
        UserCollections ret = new UserCollections();
        ret.setUserId(userId);
        String message = "Success";
        List<Integer> collectionIds = new ArrayList<Integer>();
        List<ManMadeObject> objects = new ArrayList<ManMadeObject>();
        try{
            // 根据id 获取User
            User user = userDao.getUserById(userId);

            // 获取收藏列表
            List<Collection> collections = user.getCollections();
            for(Collection collection : collections){
                Integer cId = collection.getId();
                collectionIds.add(cId);
                String uri = collection.getObject_uri();
                ManMadeObject object = manMadeObjectService.getManMadeObjectByURI(uri);
                objects.add(object);
            }
        }catch (Exception e){
            message = "Exception";
        }

        ret.setCollectionIds(collectionIds);
        ret.setObjects(objects);
        ret.setMessage(message);

        return ret;
    }
}
