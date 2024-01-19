package com.noxapps.dinnerroulette3.commons

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.input.Query
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
import io.objectbox.Box

open class BaseViewModel: ViewModel() {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)

    val mInterstitialAd: MutableState<InterstitialAd?> = mutableStateOf(null)

    fun generateQuestion(input: Query):String{
        var question = "give me a recipe for a "
        var meatFlag = 0
        var carbFlag = 0
        if (input.cuisine!="(Optional)")question+="${input.cuisine} "
        when(input.meatContent){
            "Vegetarian" -> question+="Vegetarian "
            "Vegan" -> question+="Vegan "
            "Select..." -> return ""
            else ->{
                meatFlag =1
                if (input.primaryMeat=="Any")question+= "meat "
                else question += input.primaryMeat+ " "
            }
        }
        if(input.primaryCarb!="None") {

            if (input.primaryCarb == "Any") carbFlag = 1
            if (input.primaryCarb == "Other") carbFlag = 2
            if (meatFlag == 1&&carbFlag==0) question += "and "
            if(carbFlag==0) question += input.primaryCarb + " "
        }
        question+="dish "
        if (carbFlag==1) question+= "with any carbohydrate "
        if (carbFlag==2) question+= "with an unconventional carbohydrate "
        if(input.primaryCarb=="None") question+= "with no carbohydrates "

        if (input.additionalIngredients.size>0||input.excludedIngredients.size>0||input.descriptiveTags.size>0)question+="that "
        if (input.additionalIngredients.size>0) {
            question += "includes the following ingredients: "
            input.additionalIngredients.forEach { s -> question += "$s, " }
        }
        if (input.additionalIngredients.size>0&&input.excludedIngredients.size>0)question+="; and "

        if (input.excludedIngredients.size>0) {
            question += "excludes the following ingredients: "
            input.excludedIngredients.forEach { s -> question += "$s, " }
        }
        if (input.additionalIngredients.size>0&&input.descriptiveTags.size>0)question+="; and "

        if (input.descriptiveTags.size>0) {
            question += "fits the following descriptors: "
            input.descriptiveTags.forEach { s -> question += "$s, " }
        }
        if(input.servingSizes!=Pair(0,0)){
            question+=". The recipe must make enough to serve"
            if (input.servingSizes.first>0) {
                question+= input.servingSizes.first.toString()+" adults"
                if (input.servingSizes.second>0) question+=" and"
            }
            if (input.servingSizes.second>0) {
                question+= input.servingSizes.second.toString()+" children"
            }
        }
        question += when(input.budget){
            1-> ". This dish should cost less than $20. Do not mention this cost anywhere in the recipe."
            2-> ". This dish should cost between $15 and $40. Do not mention this cost anywhere in the recipe."
            3-> ". This dish should cost more than $50. Do not mention this cost anywhere in the recipe."
            else -> ""
        }
        question+="[fin]"


        return question
    }

    fun presetRequest(promptText:String, presetId:Long, modifiers: SnapshotStateList<String>?):String{
        val thisPreset = presetBox[presetId]
        val constructedExclusions = thisPreset.enabledCarb + thisPreset.enabledMeat +
                thisPreset.excludedIngredients

        var request = "Give me a "
        when(thisPreset.meatContent){
            1 -> request+="Vegetarian "
            2 -> request+="Vegan "
            else->Unit
        }
        request += "recipe for $promptText. "
        if (constructedExclusions.isNotEmpty()) {
            request+= "Exclude the following ingredients: $constructedExclusions If an ingredient" +
                    " is important, provide the best alternative you can. "
        }
        if (thisPreset.descriptiveTags.isNotEmpty()) {
            request += "The recipe should fit the following descriptors: ${thisPreset.descriptiveTags}."
        }
        if (modifiers?.isNotEmpty() == true){
            request += "apply the following modifiers to the recipe: $modifiers"
        }
        request+="[fin]"

        return request
    }
}