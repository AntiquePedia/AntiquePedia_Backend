package com.example.antiquepedia_backend.Entity;

import lombok.Data;

import java.util.List;

@Data
public class ManMadeObject {
    private String URI;

    private List<String> labels;

    private List<String> representations;

    private List<String> refers;

}
