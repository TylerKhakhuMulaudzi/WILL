package com.example.secondsemster

data class ItemsModel(
    var title:String = "",
    var description: String = "",
    var picUrl:ArrayList<String> = ArrayList(),
    var size:ArrayList<String> = ArrayList(),
    var price: Double= 0.0,
    var rating: Double=0.0,
    var numberInCart: Int = 0
)
