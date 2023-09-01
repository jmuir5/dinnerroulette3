package com.noxapps.dinnerroulette3

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.MissingFieldException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.Date
import kotlin.random.Random

/**
 * viewModel containing functions for classic input and new input(nyi)
 */
class InputViewModel: ViewModel() {
    init{}
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val TAG1 = "Request Recipe Interstitial"
    val TAG2 = "Build Recipe Interstitial"

    val mInterstitialAd:MutableState<InterstitialAd?> = mutableStateOf(null)

    /**
     * generate a question to submit to chat gpt to generate a recipe based on query paramaters.
     * used by classic input
     */

    fun generateQuestion(input:Query):String{
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
        question+="[fin]"


        return question
    }

    /**
     * generate a recipe using chat gpt based on a specific recipe request.
     * pulls up a processing dialogue while waiting for a response from chat gpt then navigates to
     * the appropriate recipe page once its created.
     */
    fun executeRequest(promptText:String, flag: MutableState<Boolean>, context:Context, navController:NavHostController){
        var request = "Give me a recipe for $promptText"
        Log.d("constructed question", request)
        flag.value = true

        getResponse(request, context, 1) {
            var received = SavedRecipe()
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