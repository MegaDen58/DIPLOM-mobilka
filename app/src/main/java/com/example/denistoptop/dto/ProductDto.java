package com.example.denistoptop.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private List<String> images;

    private String description;
    private int count;
    private boolean winter;
    private boolean summer;
}
