package com.noxapps.dinnerroulette3.input

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.gpt.getResponse
import com.noxapps.dinnerroulette3.gpt.parseResponse
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
import com.noxapps.dinnerroulette3.usedTokens
import io.objectbox.Box
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import kotlin.random.Random

/**
 * viewModel containing functions for classic input and new input(nyi)
 */
class InputViewModel: ViewModel() {
    init{}
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)

    val meatContentItems = listOf("Select...", "Yes", "Optional", "Vegetarian", "Vegan")
    val primaryMeatItems = listOf(
        "Beef",
        "Chicken",
        "Pork",
        "Lamb",
        "Shellfish",
        "Salmon",
        "White Fish"
    )
    val primaryCarbItems =
        listOf("Pasta", "Potato", "Rice", "Noodles", "Bread")

    val budgetItems = listOf("Select...","$","$$","$$$")



    val TAG1 = "Request Recipe Interstitial"
    val TAG2 = "Build Recipe Interstitial"

    val mInterstitialAd:MutableState<InterstitialAd?> = mutableStateOf(null)

    /**
     * generate a question to submit to chat gpt to generate a recipe based on query paramaters.
     * used by classic input
     */

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
        if(input.servingsizes!=Pair(0,0)){
            question+=". The recipe must make enough to serve"
            if (input.servingsizes.first>0) {
                question+= input.servingsizes.first.toString()+" adults"
                if (input.servingsizes.second>0) question+=" and"
            }
            if (input.servingsizes.second>0) {
                question+= input.servingsizes.second.toString()+" children"
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

    fun presetRequest(promptText:String, presetId:Long):String{
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
            request += "The recipe should fit the following descriptors: ${thisPreset.descriptiveTags}"
        }
        request+="[fin]"

        return request
    }

    /**
     * generate a recipe using chat gpt based on a specific recipe request.
     * pulls up a processing dialogue while waiting for a response from chat gpt then navigates to
     * the appropriate recipe page once its created.
     */
    fun executeRequest(promptText:String, flag: MutableState<Boolean>, presetId:Long, context:Context, navController:NavHostController){
        var request = if (presetId <2){
            "Give me a recipe for $promptText"
        }
        else{
            presetRequest(promptText, presetId)
        }
        Log.d("constructed question", request)
        flag.value = true

        getResponse(request, context, 1) {
            val received: SavedRecipe
            try {
                received = SavedRecipe(QandA(Query(), it, parseResponse(it)))
                Log.e("id before", received.id.toString())
                val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
                recipeBox.put(received)

                runBlocking {
                    context.dataStore.edit { settings ->
                        val currentCounterValue = settings[usedTokens] ?: 0
                        settings[usedTokens] =
                            currentCounterValue + it.usage.total_tokens
                    }
                }

                MainScope().launch {
                    Log.e("id after", received.id.toString())
                    navController.navigate(Paths.Recipe.Path+"/"+received.id)
                }
            } catch (e:Exception){
                MainScope().launch {
                    navController.navigate(Paths.Error.Path+"/"+it.choices[0].message.content)
                }
            }
        }
    }

    /**
     * generate a recipe using chat gpt based on a classic input query.
     * pulls up a processing dialogue while waiting for a response from chat gpt then navigates to
     * the appropriate recipe page once its created.
     */
    fun executeClassic(query: Query, flag: MutableState<Boolean>, context:Context, navController:NavHostController){
        var question2 = generateQuestion(query)
        Log.d("constructed question", question2)
        flag.value = true

        getResponse(question2, context, 0) { it ->
            var received = SavedRecipe()
            try {
                received = SavedRecipe(QandA(query, it, parseResponse(it)))
                Log.e("id before", received.id.toString())
                val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
                recipeBox.put(received)

                runBlocking {
                    context.dataStore.edit { settings ->
                        val currentCounterValue = settings[usedTokens] ?: 0
                        settings[usedTokens] =
                            currentCounterValue + it.usage.total_tokens
                    }
                }

                MainScope().launch {
                    Log.e("id after", received.id.toString())
                    navController.navigate(Paths.Recipe.Path+"/"+received.id)
                }
            } catch (e:Exception){
                MainScope().launch {
                    navController.navigate(Paths.Error.Path+"/"+it.choices[0].message.content)
                }
            }



        }
    }

    /**
     * simple function to return a string containing a random recipe name for use with the recipe
     * request page
     */

    fun randomDishName():String{
        val dishes = arrayOf("Lamb Rogan Josh", "Chicken Pizza", "Cheeseburger and Chips",
            "Lemon Pie","Butter Chicken", "Mapo Tofu", "Vegan Eggs and Bacon", "IceCream",
            "Cajun Shrimp Boil", "Spaghetti Bolognese", "Fettuccine Carobonara", "Nachos",
            "Buffalo Wings", "Chicken Pesto Pasta")
        return dishes[Random(Date().time).nextInt(dishes.size)]
    }

}