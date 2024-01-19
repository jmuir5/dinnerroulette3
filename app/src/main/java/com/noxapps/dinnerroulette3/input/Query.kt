package com.noxapps.dinnerroulette3.input

import com.noxapps.dinnerroulette3.gpt.GptResponse
import com.noxapps.dinnerroulette3.recipe.SavedRecipe


/**
 * query object intended to be used to hold info from input to use in question generation
 * and origonally intended to be used to help with search, though it probably will not be
 * meaningfully used that way.
 */

class Query(
    var meatContent: String = "",
    var primaryMeat: String = "",
    var primaryCarb: String = "",
    var cuisine: String = "",
    var servingSizes: Pair<Int, Int> = Pair(0,0),
    var additionalIngredients: MutableList<String> = mutableListOf(),
    var excludedIngredients: MutableList<String> = mutableListOf(),
    var descriptiveTags: MutableList<String> = mutableListOf(),
    var budget: Int = 0
) {
    constructor(recipe: SavedRecipe) : this(){
        meatContent = recipe.meatContent?:""
        primaryMeat = recipe.primaryMeat?:""
        primaryCarb = recipe.primaryCarb?:""
        cuisine = recipe.cuisine?:""
        servingSizes = Pair(recipe.adultServes?:0,recipe.childServes?:0)
        additionalIngredients = recipe.additionalIngredients
        excludedIngredients = recipe.excludedIngredients
        descriptiveTags = recipe.descriptiveTags
        budget = recipe.budget?:0


    }
    operator fun set(i: Int, value: String) {

    }
    override operator fun equals(other:Any?):Boolean {
        if (other !is Query) return false
        return this.meatContent==other.meatContent &&
         this.primaryMeat == other.primaryMeat &&
         this.primaryCarb == other.primaryCarb &&
         this.cuisine ==other.cuisine &&
         this.servingSizes ==other.servingSizes &&
         this.additionalIngredients ==other.additionalIngredients &&
         this.excludedIngredients ==other.excludedIngredients &&
         this.descriptiveTags ==other.descriptiveTags &&
         this.budget ==other.budget
    }
}


class QandA(
    val question: Query,
    val raw: GptResponse,
    val parsed: ParsedResponse
)

class ParsedResponse(
    val title:String,
    val description:String,
    val ingredients:String,
    val method:String,
    val notes:String,
    val image:String
)



