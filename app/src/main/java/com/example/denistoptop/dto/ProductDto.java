package com.example.denistoptop.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private List<String> images;

    private String description;
    private int count;
    private boolean winter;
    private boolean summer;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setWinter(boolean winter) {
        this.winter = winter;
    }

    public void setSummer(boolean summer) {
        this.summer = summer;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public boolean isWinter() {
        return winter;
    }

    public boolean isSummer() {
        return summer;
    }

    // Создаем конструктор, который принимает все поля класса
    public ProductDto(String name, String description, int count, boolean winter, boolean summer, List<String> images) {
        this.name = name;
        this.description = description;
        this.count = count;
        this.winter = winter;
        this.summer = summer;
        this.images = images;
    }
    public ProductDto(){

    }
}
