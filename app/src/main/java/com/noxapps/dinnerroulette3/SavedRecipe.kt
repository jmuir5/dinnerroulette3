package com.noxapps.dinnerroulette3

import org.bson.types.ObjectId
import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.PrimaryKey



open class SavedRecipe(
    var meatContent:String? = "",
    var primaryMeat:String? = "",
    var primaryCarb:String? = "",
    var cuisine:String? = "",
    var additionalIngredients: RealmList<String> = RealmList(""),
    var excludedIngredients:RealmList<String> = RealmList(""),
    var descriptiveTags:RealmList<String> = RealmList(""),

    var title:String? = "",
    var description:String? = "",
    var ingredients:String? = "",
    var method:String? = "",
    var notes:String? = ""
    ) : RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    constructor(recipe:QandA) : this() {
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
    }

}

val defaultQandA = QandA(
    Query("Optional", "Any", "Any", "(Optional)", mutableListOf<String>(), mutableListOf<String>(), mutableListOf<String>()),
    GptResponse("default", "default response", 0,"default", listOf(
        GptChoices(0,
            GptMessage("0", "0"),"finish")
    ),
        GptUsage(1, 1, 2) ),
    ParsedResponse("1","2", "3", "4", "5"))