package com.example.antiquepedia_backend.dao;

import com.example.antiquepedia_backend.Entity.Collection;

public interface CollectionDao {
    // 收藏
    public void addCollection(Collection collection);

    // 取消收藏
    public void removeCollection(Integer id);
}
