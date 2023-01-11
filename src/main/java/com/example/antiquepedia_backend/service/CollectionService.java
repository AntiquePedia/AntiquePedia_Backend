package com.example.antiquepedia_backend.service;

import com.example.antiquepedia_backend.Entity.Collection;

public interface CollectionService {
    public String addCollection(Collection collection);
    public String removeCollection(Integer id);
}

