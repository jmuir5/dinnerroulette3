package com.noxapps.dinnerroulette3
import kotlinx.serialization.Serializable
class Query(
    var meatContent:String = "",
    var primaryMeat:String = "",
    var primaryCarb:String = "",
    var additionalIngredients:MutableList<String> = mutableListOf(),
    var excludedIngredients:MutableList<String> = mutableListOf(),
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

@Serializable
class Settings(
    val imperial: Boolean,
    val fahrenheit: Boolean,
    val allergens:List<String>
)