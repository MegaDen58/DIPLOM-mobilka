package com.example.denistoptop

import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.dto.UserDto

class GlobalVariables {
    companion object{
        var userId: Long = 1
        var user: UserDto? = null
        var allCartPrice: Int = 0
        var cart: MutableList<ProductDto> = mutableListOf()
    }
}