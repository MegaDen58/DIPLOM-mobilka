package com.example.denistoptop.dto;

import lombok.Data;
@Data
public class UserDto {
    Long id;
    String login;
    String password;
    String email;

    public UserDto(String login, String password){
        this.login = login;
        this.password = password;
    }
    public UserDto() {
    }
    public UserDto(String login, String password, String email){
        this.login = login;
        this.password = password;
        this.email = email;
    }

}
