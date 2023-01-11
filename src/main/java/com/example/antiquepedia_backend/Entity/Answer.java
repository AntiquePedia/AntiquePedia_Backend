package com.example.antiquepedia_backend.Entity;

import lombok.Data;

import java.util.List;

@Data
public class Answer {
    private String content;
    private List<String> links;
}
