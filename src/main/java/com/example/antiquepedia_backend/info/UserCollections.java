package com.example.antiquepedia_backend.info;

import com.example.antiquepedia_backend.Entity.ManMadeObject;
import lombok.Data;

import java.util.List;

@Data
public class UserCollections {
    private Integer userId;
    private String message;
    private List<Integer> collectionIds;
    private List<ManMadeObject> objects;

}