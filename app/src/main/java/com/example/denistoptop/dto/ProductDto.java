package com.example.denistoptop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
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

    // Создаем конструктор, который принимает все поля класса
    public ProductDto(String name, String description, int count, boolean winter, boolean summer, List<String> images) {
        this.name = name;
        this.description = description;
        this.count = count;
        this.winter = winter;
        this.summer = summer;
        this.images = images;
    }
}
