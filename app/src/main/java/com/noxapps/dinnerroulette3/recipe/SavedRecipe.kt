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
public class SavedRecipe(
    @Id
    var id:Long =0,
    var favourite:Boolean = false,
    var meatContent:String? = "",
    var primaryMeat:String? = "",
    var primaryCarb:String? = "",
    var cuisine:String? = "",
    var additionalIngredients: MutableList<String> = mutableListOf(),
    var excludedIngredients: MutableList<String> = mutableListOf(),
    var descriptiveTags: MutableList<String> = mutableListOf(),

    var title:String? = "",
    var description:String? = "",
    var ingredients:String? = "",
    var method:String? = "",
    var notes:String? = "",
    var image:String? = "",
    var imageDescription:String?=""
    )  {

    constructor(recipe: QandA, image:String?) : this() {
        meatContent = recipe.question.meatContent
        primaryMeat = recipe.question.primaryMeat
        primaryCarb = recipe.question.primaryCarb
        cuisine = recipe.question.cuisine
        recipe.question.additionalIngredients.forEach { item->
            additionalIngredients.add(item)
        }
        recipe.question.excludedIngredients.forEach { item->
            excludedIngredients.add(item)
        }
        recipe.question.descriptiveTags.forEach { item->
            descriptiveTags.add(item)
        }
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
        recipe.question.additionalIngredients.forEach { item->
            additionalIngredients.add(item)
        }
        recipe.question.excludedIngredients.forEach { item->
            excludedIngredients.add(item)
        }
        recipe.question.descriptiveTags.forEach { item->
            descriptiveTags.add(item)
        }
        title = recipe.parsed.title
        description = recipe.parsed.description
        ingredients = recipe.parsed.ingredients
        method = recipe.parsed.method
        notes = recipe.parsed.notes
        imageDescription = recipe.parsed.image

    }

}

val defaultQandA = QandA(
    Query("Optional", "Any", "Any", "(Optional)", mutableListOf<String>(), mutableListOf<String>(), mutableListOf<String>()),
    GptResponse("default", "default response", 0,"default", listOf(
        GptChoices(0,
            GptMessage("0", "0"),"finish")
    ),
        GptUsage(1, 1, 2) ),
    ParsedResponse("1","2", "3", "4", "5", "")
)