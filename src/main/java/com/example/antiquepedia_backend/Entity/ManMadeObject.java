package com.example.antiquepedia_backend.Entity;

import lombok.Data;

import java.util.List;

@Data
public class ManMadeObject {
    private String URI;
    private List<String> labels;
    private List<String> representations;
    private List<String> refers;

    // 有什么材料组成(list)
    private List<String> materials;

    // production time span
    private String period_begin;
    private String period_end;

    // has type (list)
    private List<String> has_type;

    // place
    private Place place;
}
