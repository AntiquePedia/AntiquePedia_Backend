package com.example.antiquepedia_backend.repository;

import com.example.antiquepedia_backend.Entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Integer> {
}
