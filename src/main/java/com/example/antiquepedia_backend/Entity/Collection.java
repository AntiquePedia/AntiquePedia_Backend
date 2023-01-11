package com.example.antiquepedia_backend.Entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "collection")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "object_uri")
    private String object_uri;

    @Column(name = "user_id")
    private Integer user_id;
}