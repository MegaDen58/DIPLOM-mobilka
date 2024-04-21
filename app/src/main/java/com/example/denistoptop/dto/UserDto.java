package com.example.denistoptop.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
public class UserDto {
    Long id;
    String login;

    String password;
    String email;

    List<Integer> favourites;

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<Integer> getFavourites() {
        return favourites;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFavourites(List<Integer> favourites) {
        this.favourites = favourites;
    }

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
