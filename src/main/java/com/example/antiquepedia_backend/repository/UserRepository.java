package com.example.antiquepedia_backend.repository;

import com.example.antiquepedia_backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserById(Integer id);

    User getUserByName(String name);
}
