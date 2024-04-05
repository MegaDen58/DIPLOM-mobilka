package com.example.denistoptop.model

class User(val id: Int, val login: String, val password: String) {
    // Вторичный конструктор для создания объекта без id
    constructor(login: String, password: String) : this(-1, login, password)
}