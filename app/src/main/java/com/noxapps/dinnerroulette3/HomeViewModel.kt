package com.noxapps.dinnerroulette3

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import io.objectbox.Box
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import kotlin.math.abs
import kotlin.random.Random

/**
 * view model for the home page, holding home page related functions and variables
 */
class HomeViewModel: ViewModel() {

    val recipeBox: Box<SavedRecipe> = ObjectBox.store.boxFor(SavedRecipe::class.java)

    /**
     * generates a list of 5, random favourited recipes
     */
    fun faveFive():List<Long>{
        val query = recipeBox.query(SavedRecipe_.favourite.equal(true)).build()
        val orderedFaves = query.findIds()
        val randomFaves = mutableListOf<Long>()
        if(orderedFaves.isNotEmpty()) {
            while (randomFaves.size < 5) {
                val randInt = Random.nextInt(orderedFaves.size)
                if (!randomFaves.contains(orderedFaves[randInt])) {
                    randomFaves.add(orderedFaves[randInt])
                }
                if (randomFaves.size == orderedFaves.size) break
            }
        }
        return randomFaves
    }

    /**
     * generates a list of the last 5 generated recipes
     */
    fun lastFive():List<Long>{
        val allRecipes = recipeBox.all
        val lastFive:MutableList<Long> = mutableListOf()
        if(allRecipes.size>=5) {
            for (i in allRecipes.size downTo (allRecipes.size-4)) {
                lastFive.add(recipeBox[(i).toLong()].id)
            }
        }
        else for(i in allRecipes.size downTo 1){
            lastFive.add(recipeBox[i.toLong()].id)
        }
        return lastFive
    }

    /**
     * returns a random saved recipe
     */
    fun randomSaved():Long{
        val allRecipes = recipeBox.all
        return allRecipes[Random.nextInt(allRecipes.size)].id
    }

    /**
     * returns a random favourited saved recipe
     */
    fun randomFavourite():Long{
        val query = recipeBox.query(SavedRecipe_.favourite.equal(true)).build()
        val allFaves = query.findIds()
        return allFaves[Random.nextInt(allFaves.size)]
    }

    /**
     * generate a random recipe using chat gpt. pulls up a processing dialogue while waiting for a
     * response from chat gpt then navigates to the apropriate recipe page once its created
     */
    fun executeRandom(flag: MutableState<Boolean>, context: Context, navController: NavHostController){
        var randQuery = getRandomQuery(abs(Random(Date().time).nextInt()))
        val randQuestion = getRandomQuestion(randQuery)
        flag.value = true

        getResponse(randQuestion, context, 1) {
            var received = SavedRecipe()
            try {
                received = SavedRecipe(QandA(randQuery, it, parseResponse(it)))
            } catch (e:IndexOutOfBoundsException){
                navController.navigate(Paths.Error.Path+"/"+it.choices[0].message.content)
            }
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


        }
    }

    /**
     * generate a randomised prompt for use with executeRandom()
     */
    fun getRandomQuery(seed:Int):Query{
        val cuisines=listOf("Chinese","Chinese","Chinese","Chinese","Chinese","Chinese","Chinese",
            "Chinese","Chinese","Chinese","Indian","Indian","Indian","Indian","Indian","Indian",
            "Indian","Indian","Indian","Indian","Japanese","Japanese","Japanese","Japanese",
            "Japanese","Japanese","Japanese","Japanese", "Thai","Thai","Thai","Thai","Thai","Thai",
            "Thai","Thai","Korean","Korean","Korean","Korean","Korean","Korean","Vietnamese",
            "Vietnamese","Vietnamese","Vietnamese","Filipino","Malaysian","Indonesian","Pakistani","Iranian","Afghan","SriLankan","Bangladeshi",
            "Nepalese","Bhutanese","Mongolian","Tibetan","Cambodian","Italian","French","Italian",
            "French","Italian","French","Italian","French","Italian","French","Italian","French",
            "Italian","French","Italian","French","Italian","French","Italian","French","Spanish",
            "Spanish","Spanish","Spanish","Spanish","Spanish","Spanish","Spanish","Greek","Greek",
            "Greek","Greek","Greek","Greek","Turkish","British","German","British","German",
            "British","German","British","German","British","German","British","German","Russian",
            "Russian","Russian","Russian","Finnish","Swedish","Norwegian","Polish","Hungarian",
            "Lebanese","Lebanese","Lebanese","Lebanese","Lebanese","Lebanese","Lebanese","Lebanese",
            "Israeli","Egyptian","Israeli","Egyptian","Israeli","Egyptian","Israeli","Egyptian",
            "Moroccan","Moroccan","Moroccan","Moroccan","Mexican","Mexican","Mexican","Mexican",
            "Mexican","Mexican","Mexican","Mexican","Mexican","Mexican","Italian-American",
            "Italian-American","Italian-American","Italian-American","Brazilian","Peruvian",
            "Argentinian","Colombian","Chilean","Brazilian","Peruvian", "Argentinian","Colombian",
            "Chilean","Brazilian","Peruvian","Argentinian","Colombian","Chilean","Ethiopian",
            "South African","Caribbean","Australian","Caribbean","Australian","Caribbean",
            "Australian","Caribbean","Australian","Caribbean","Australian","Caribbean","Australian",
            "Caribbean","Australian","Caribbean","Australian","Cuban","Russian","Finnish",
            "Norwegian","Hungarian","Tibetan","Afghan","Bhutanese","Kuwaiti","SaudiArabian",
            "Emirati","Omani","Qatari","Bahraini","Jordanian","Iraqi","Palestinian","Yemeni")
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

        var vegFlag = "Yes"

        if(Random(Date().time).nextInt(50)==0) {
            vegFlag="Vegan"
        }
        else if(Random(Date().time).nextInt(10)==0){
            vegFlag="Vegetarian"
        }
        return Query(
            vegFlag,
            protein[seed%protein.size],
            "Any",
            cuisines[seed%cuisines.size],
            mutableListOf(),
            mutableListOf(),
            mutableListOf(descriptors[seed%descriptors.size])
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
        question += query.descriptiveTags[0]+".[fin]"
        return question
    }
}


