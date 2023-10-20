package com.noxapps.dinnerroulette3.home

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
import com.noxapps.dinnerroulette3.input.QandA
import com.noxapps.dinnerroulette3.input.Query
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import com.noxapps.dinnerroulette3.input.SettingsObject
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.gpt.getResponse
import com.noxapps.dinnerroulette3.gpt.parseResponse
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.usedTokens
import io.objectbox.Box
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Date
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

/**
 * view model for the home page, holding home page related functions and variables
 */
class HomeViewModel: ViewModel() {

    val recipeBox: Box<SavedRecipe> = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val TAG = "Home Page Interstitial"

    val mInterstitialAd:MutableState<InterstitialAd?> = mutableStateOf(null)


    /**
     * generates a list of 5, random favourited recipes
     */

    /**
     * generate a random recipe using chat gpt. pulls up a processing dialogue while waiting for a
     * response from chat gpt then navigates to the apropriate recipe page once its created
     */
    fun executeRandom(flag: MutableState<Boolean>, context: Context, navController: NavHostController){
        var randQuery = getRandomQuery(abs(nextInt()), context)
        val randQuestion = getRandomQuestion(randQuery)
        flag.value = true

        getResponse(randQuestion, context, 1) {
            var received = SavedRecipe()
            try {
                received = SavedRecipe(QandA(randQuery, it, parseResponse(it)))
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
            } catch (e:IndexOutOfBoundsException){
                navController.navigate(Paths.Error.Path+"/"+it.choices[0].message.content)
            }



        }
    }

    /**
     * generate a randomised prompt for use with executeRandom()
     */
    fun getRandomQuery(seed:Int, context: Context): Query {

        val chinese = (0..10).map { "Chinese" }
        val indian = (0..10).map { "Indian" }
        val japanese = (0..8).map { "Japanese" }
        val thai = (0..8).map { "Thai" }
        val korean = (0..6).map { "Korean" }
        val vietnamese = (0..4).map { "Vietnamese" }
        val italian = (0..10).map { "Italian" }
        val french = (0..10).map { "French" }
        val spanish = (0..8).map { "Spanish" }
        val greek = (0..6).map { "Greek" }
        val british = (0..6).map { "British" }
        val german = (0..6).map { "German" }
        val russian = (0..4).map { "Russian" }
        val lebanese = (0..8).map { "Lebanese" }
        val israeli = (0..4).map { "Israeli" }
        val egyptian = (0..4).map { "Egyptian" }
        val moroccan = (0..4).map { "Moroccan" }
        val mexican = (0..10).map { "Mexican" }
        val itamerican = (0..4).map { "Italian-American" }
        val brazilian = (0..4).map { "Brazilian" }
        val peruvian = (0..4).map { "Peruvian" }
        val argentinian = (0..4).map { "Argentinian" }
        val colombian = (0..4).map { "Colombian" }
        val chilean = (0..4).map { "Chilean" }
        val caribbean = (0..8).map { "Caribbean" }
        val australian = (0..8).map { "Australian" }
        val cuisines = chinese + indian + japanese + thai + korean + vietnamese +italian+french+
                spanish+greek+british+german+russian+lebanese+israeli+ egyptian+moroccan+
                mexican+itamerican+brazilian+peruvian+argentinian+colombian +chilean+caribbean+
                australian+
                listOf( "Filipino","Malaysian","Indonesian","Pakistani","Iranian","Afghan",
                    "SriLankan","Bangladeshi","Nepalese","Bhutanese","Mongolian","Tibetan",
                    "Cambodian","Turkish","Finnish","Swedish","Norwegian","Polish","Hungarian", 
                    "Ethiopian", "South African","Cuban","Russian","Finnish", "Norwegian",
                    "Hungarian","Tibetan","Afghan","Bhutanese","Kuwaiti","SaudiArabian", "Emirati",
                    "Omani","Qatari","Bahraini","Jordanian","Iraqi","Palestinian","Yemeni")

        val protein = listOf("Chicken","Beef","Chicken","Beef","Chicken","Beef","Chicken","Beef",
            "Chicken","Beef","Chicken","Beef","Chicken","Beef","Chicken","Beef","Chicken","Beef",
            "Chicken","Beef","Pork","Pork","Pork","Pork","Pork","Pork","Pork","Pork","Lamb","Lamb",
            "Lamb","Lamb","Lamb","Lamb","Seafood","Seafood","Seafood","Seafood","Shellfish",
            "Shellfish","Salmon","Salmon","White Fish","White Fish","Eggs","Eggs","Legumes",
            "Legumes")
        val descriptors = listOf("Warm","Homestyle","Hearty","Nurturing","Comfort food","Homey",
            "Inviting","Wholesome","Rustic","Satisfying","Zesty","Hot","Piquant","Fiery","Spicy",
            "Bold","Tongue-tingling","Peppery","Flaming","Searing","Rich","Decadent","Luxurious",
            "Indulgent","Opulent","Gourmet","Lavish","Sumptuous","Velvety","Extravagant","Luscious",
            "Eclectic","Daring","Exotic","Adventurous","Thrilling","Unconventional","Unexpected",
            "Novel","Innovative","Bold")

        var meatContentIndex = 0
        var budgetIndex = 0
        val loadedData = runBlocking { context.dataStore.data.first() }

        loadedData[savedPreferences]?.let {
            val retrievedData: SettingsObject = try {
                Json.decodeFromString<SettingsObject>(it)
            }catch(exception: Exception){
                SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
            }
            meatContentIndex = retrievedData.meatContent
            budgetIndex = if(retrievedData.budget>0) retrievedData.budget
                else seed%4
        }

        var vegFlag = "Yes"
        when(meatContentIndex){
            0->{
                if(Random(Date().time).nextInt(50)==0) {
                vegFlag="Vegan"
                }
                else if(Random(Date().time).nextInt(10)==0){
                    vegFlag="Vegetarian"
                }
            }
            1->Unit
            2->{
                vegFlag="Vegetarian"
                if(Random(Date().time).nextInt(10)==0) {
                    vegFlag="Vegan"
                }
            }
            3->vegFlag="Vegan"
        }


        return Query(
            vegFlag,
            protein[seed%protein.size],
            "Any",
            cuisines[seed%cuisines.size],
            mutableListOf(),
            mutableListOf(),
            mutableListOf(descriptors[seed%descriptors.size]),
            budgetIndex
        )
    }
    fun getRandomQuestion(query: Query):String{
        var question = "give me a recipe for a "

        question+=query.cuisine+" "

        if(query.meatContent == "Yes") {
            question+=query.primaryMeat+" "
        }
        else {
            question+=query.meatContent+" "
        }

        question += "dish that could be described as "
        question += query.descriptiveTags[0]
        question += when(query.budget){
            1-> " and costs less than $20. Do not mention this cost anywhere in the recipe.[fin]"
            2-> " and costs between $15 and $40. Do not mention this cost anywhere in the recipe.[fin]"
            3-> " and costs more than $50. Do not mention this cost anywhere in the recipe.[fin]"
            else -> ".[fin]"
        }
        return question
    }
}


