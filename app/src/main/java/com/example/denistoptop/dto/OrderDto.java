package com.example.denistoptop.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OrderDto implements Serializable {
    private Long id;

    private Date start_date;
    private Date end_date;

    private Integer price;
    private Integer user_id;
    private String type;
    private List<Integer> items;

    public OrderDto(Long id, Integer price, String type){
        this.id = id;
        this.price = price;
        this.type = type;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", price=" + price +
                ", user_id=" + user_id +
                ", type='" + type + '\'' +
                ", items=" + items +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getType() {
        return type;
    }

    public List<Integer> getItems() {
        return items;
    }
}
