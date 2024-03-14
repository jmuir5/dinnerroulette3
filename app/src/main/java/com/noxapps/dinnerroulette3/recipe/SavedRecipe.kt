package com.noxapps.dinnerroulette3.recipe

import com.noxapps.dinnerroulette3.gpt.GptChoices
import com.noxapps.dinnerroulette3.gpt.GptMessage
import com.noxapps.dinnerroulette3.gpt.GptResponse
import com.noxapps.dinnerroulette3.gpt.GptUsage
import com.noxapps.dinnerroulette3.input.ParsedResponse
import com.noxapps.dinnerroulette3.input.QandA
import com.noxapps.dinnerroulette3.input.Query
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@Entity
public data class SavedRecipe(
    @Id
    var id:Long =0,
    var favourite:Boolean = false,
    var meatContent:String? = "",
    var primaryMeat:String? = "",
    var primaryCarb:String? = "",
    var cuisine:String? = "",
    var adultServes:Int? = 0,
    var childServes:Int? = 0,
    var additionalIngredients: List<String> = listOf(),
    var excludedIngredients: List<String> = listOf(),
    var descriptiveTags: List<String> = listOf(),
    var budget:Int? =0,

    var title:String? = "",
    var description:String? = "",
    var ingredients:List<String> = listOf(),
    var method:List<String> = listOf(),
    var notes:List<String> = listOf(),
    var image:String? = "",
    var imageDescription:String?="",

    var shareUrl:String?=""
    )  {

    constructor(recipe: QandA, image:String?) : this() {
        meatContent = recipe.question.meatContent
        primaryMeat = recipe.question.primaryMeat
        primaryCarb = recipe.question.primaryCarb
        cuisine = recipe.question.cuisine
        adultServes = recipe.question.servingSizes.first
        childServes = recipe.question.servingSizes.second
        additionalIngredients = recipe.question.additionalIngredients.toList()
        excludedIngredients = recipe.question.excludedIngredients.toList()
        descriptiveTags = recipe.question.descriptiveTags.toList()
        budget = recipe.question.budget

        title = recipe.parsed.title
        description = recipe.parsed.description
        ingredients = recipe.parsed.ingredients
        method = recipe.parsed.method
        notes = recipe.parsed.notes
        this.image = image
        imageDescription = recipe.parsed.image
    }
    constructor(recipe: QandA) : this() {
        meatContent = recipe.question.meatContent
        primaryMeat = recipe.question.primaryMeat
        primaryCarb = recipe.question.primaryCarb
        cuisine = recipe.question.cuisine
        adultServes = recipe.question.servingSizes.first
        childServes = recipe. question.servingSizes.second

        additionalIngredients = recipe.question.additionalIngredients.toList()
        excludedIngredients = recipe.question.excludedIngredients.toList()
        descriptiveTags = recipe.question.descriptiveTags.toList()

        budget = recipe.question.budget

        title = recipe.parsed.title
        description = recipe.parsed.description
        ingredients = recipe.parsed.ingredients
        method = recipe.parsed.method
        notes = recipe.parsed.notes
        imageDescription = recipe.parsed.image

    }

    fun nearlyEqual(other:SavedRecipe):Boolean{
        return (
            title==other.title&&
            description== other.description&&
            ingredients== other.ingredients&&
            method== other.method&&
            notes== other.notes&&
            imageDescription ==other.imageDescription
        )
    }

}

val defaultQandA = QandA(
    Query(
        "Optional",
        "Any",
        "Any",
        "(Optional)",
        Pair(0, 0),
        mutableListOf<String>(),
        mutableListOf<String>(),
        mutableListOf<String>()
    ),
    GptResponse("default", "default response", 0,"default", listOf(
        GptChoices(0,
            GptMessage("0", "0"),"finish")
    ),
        GptUsage(1, 1, 2),
        null),
    ParsedResponse("1","2", listOf("3"), listOf("4"), listOf("5"), "")
)