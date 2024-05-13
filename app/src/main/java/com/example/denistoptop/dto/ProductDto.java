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
    private int price;
    private boolean winter;
    private boolean summer;
    private String color;
    private String material;
    private String size;

    public void setColor(String color) {
        this.color = color;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public String getMaterial() {
        return material;
    }

    public String getSize() {
        return size;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getPrice(){
        return price;
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


    public boolean isWinter() {
        return winter;
    }

    public boolean isSummer() {
        return summer;
    }

    // Создаем конструктор, который принимает все поля класса
    public ProductDto(String name, String description, int count, boolean winter, boolean summer, List<String> images, int price, String material, String size, String color) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.color = color;
        this.size = size;
        this.winter = winter;
        this.summer = summer;
        this.images = images;
        this.price = price;
    }
    public ProductDto(){

    }
}
