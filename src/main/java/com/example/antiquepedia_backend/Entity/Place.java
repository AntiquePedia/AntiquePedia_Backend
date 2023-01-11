package com.example.antiquepedia_backend.Entity;

import lombok.Data;

@Data
public class Place {
    private String uri;
    private String label;
    private String fallWithin;
    private String longitude;
    private String latitude;

}
