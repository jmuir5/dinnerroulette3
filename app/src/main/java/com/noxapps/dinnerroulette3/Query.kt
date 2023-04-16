package com.noxapps.dinnerroulette3

class Query(
    var meatContent:String = "",
    var primaryMeat:String = "",
    var primaryCarb:String = "",
    var spiceContent :Int = 0,
    var cheeseContent:Int = 0,
    var glutenFree:Boolean = false,
    var lactoseFree:Boolean = false,
    var additionalIngredients:MutableList<String> = mutableListOf(),
    var descriptiveTags:MutableList<String> = mutableListOf()
) {
    operator fun set(i: Int, value: String) {

    }
}

class QandA(
    val question:Query,
    val answer:GptResponse,
    val name:String
    )