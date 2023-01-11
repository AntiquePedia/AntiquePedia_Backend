package com.example.antiquepedia_backend.dao.impl;

import com.example.antiquepedia_backend.Entity.Collection;
import com.example.antiquepedia_backend.dao.CollectionDao;
import com.example.antiquepedia_backend.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CollectionDaoImpl implements CollectionDao {
    @Autowired
    CollectionRepository collectionRepository;

    @Override
    public void addCollection(Collection collection) {
        collectionRepository.save(collection);
    }

    @Override
    public void removeCollection(Integer id) {
        collectionRepository.deleteById(id);
    }
}