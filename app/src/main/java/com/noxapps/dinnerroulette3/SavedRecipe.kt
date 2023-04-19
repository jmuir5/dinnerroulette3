package com.noxapps.dinnerroulette3

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
//import org.mongodb.kbson.ObjectId


class SavedRecipe(recipe:QandA) : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var meatContent:String = recipe.question.meatContent
    var primaryMeat:String = recipe.question.primaryMeat
    var primaryCarb:String = recipe.question.primaryCarb
    var cuisine:String = recipe.question.cuisine
    //var additionalIngredients: RealmList<String> = recipe.question.additionalIngredients.toRealmList()
    //var excludedIngredients:RealmList<String> = recipe.question.excludedIngredients.toRealmList()
    //var descriptiveTags:RealmList<String> = recipe.question.descriptiveTags.toRealmList()

    val title:String = recipe.parsed.title
    val description:String = recipe.parsed.description
    val ingredients:String = recipe.parsed.ingredients
    val method:String = recipe.parsed.method
    val notes:String = recipe.parsed.notes
    constructor() : this(

    )
}