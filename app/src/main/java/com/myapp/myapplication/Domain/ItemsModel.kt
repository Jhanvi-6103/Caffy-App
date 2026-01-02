package com.myapp.myapplication.Domain

import java.io.Serializable

data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var price: Int = 0,
    var rating: Double = 0.0,
    var numberInCart: Int = 1,
    var extra: String = "",
    var size: String? = null,
    var sizePrice: SizePrice? = null
) : Serializable {

    fun getPriceBySelectedSize(): Int {
        return when (size) {
            "Small" -> sizePrice?.small ?: price
            "Medium" -> sizePrice?.medium ?: price
            "Large" -> sizePrice?.large ?: price
            else -> price
        }
    }
}

data class SizePrice(
    var small: Int = 0,
    var medium: Int = 0,
    var large: Int = 0
) : Serializable
