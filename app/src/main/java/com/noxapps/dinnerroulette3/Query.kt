package com.noxapps.dinnerroulette3
import io.reactivex.rxjava3.internal.operators.flowable.FlowableSkipLast
import kotlinx.serialization.Serializable
class Query(
    var meatContent:String = "",
    var primaryMeat:String = "",
    var primaryCarb:String = "",
    var cuisine:String="",
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

class ParsedResponse(
    val title:String,
    val description:String,
    val ingredients:String,
    val method:String,
    val notes:String
)



@Serializable
class Settings(
    val imperial: Boolean,
    val fahrenheit: Boolean,
    val allergens:List<String>,
    val skill:Int
)