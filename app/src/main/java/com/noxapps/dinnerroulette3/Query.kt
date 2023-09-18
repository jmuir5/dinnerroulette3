package com.noxapps.dinnerroulette3

import kotlinx.serialization.Serializable


/**
 * query object intended to be used to hold info from input to use in question generation
 * and origonally intended to be used to help with search, though it probably will not be
 * meaningfully used that way.
 */

class Query(
    var meatContent:String = "",
    var primaryMeat:String = "",
    var primaryCarb:String = "",
    var cuisine:String="",
    var additionalIngredients:MutableList<String> = mutableListOf(),
    var excludedIngredients:MutableList<String> = mutableListOf(),
    var descriptiveTags:MutableList<String> = mutableListOf(),
    var budget:Int=0
) {
    operator fun set(i: Int, value: String) {

    }
}


class QandA(
    val question:Query,
    val raw:GptResponse,
    val parsed:ParsedResponse
)

class ParsedResponse(
    val title:String,
    val description:String,
    val ingredients:String,
    val method:String,
    val notes:String,
    val image:String
)



@Serializable
class SettingsObject(
    val imperial: Boolean,
    val fahrenheit: Boolean,
    val allergens:List<String>,
    val skill:Int,
    val dietPreset:Long,
    val meatContent:Int,
    val budget:Int

)