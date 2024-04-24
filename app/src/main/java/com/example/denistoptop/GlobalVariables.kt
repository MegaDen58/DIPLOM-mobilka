package com.example.denistoptop

import com.example.denistoptop.dto.ProductDto

class GlobalVariables {
    companion object {
        var userId: Long = 1
        var cart: MutableList<ProductDto> = mutableListOf()
    }
}