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
}
