package com.example.antiquepedia_backend.service.impl;

import com.example.antiquepedia_backend.Entity.Collection;
import com.example.antiquepedia_backend.dao.CollectionDao;
import com.example.antiquepedia_backend.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    CollectionDao collectionDao;

    @Override
    public String addCollection(Collection collection) {
        String retStr = "Success";
        try{
            collectionDao.addCollection(collection);
        }catch (Exception e){
            retStr = "Exception";
        }

        return retStr;
    }

    @Override
    public String removeCollection(Integer id) {
        String retStr = "Success";
        try{
            collectionDao.removeCollection(id);
        }catch (Exception e){
            retStr = "Exception";
        }

        return retStr;
    }
}
