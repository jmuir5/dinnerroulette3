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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random

class HomeViewModel: ViewModel() {

    val recipeBox: Box<SavedRecipe> = ObjectBox.store.boxFor(SavedRecipe::class.java)
    fun randomFave():List<Long>{
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

    fun randomSaved():Long{
        val allRecipes = recipeBox.all
        return allRecipes[Random.nextInt(allRecipes.size)].id
    }

    fun randomFavourite():Long{
        val query = recipeBox.query(SavedRecipe_.favourite.equal(true)).build()
        val allFaves = query.findIds()
        return allFaves[Random.nextInt(allFaves.size)]
    }


    fun executeRandom(flag: MutableState<Boolean>, context: Context, navController: NavHostController){
        var request = "Give me a random recipe give it an appropriate name"
        flag.value = true

        getResponse(request, context, 1) {
            var received = SavedRecipe()
            try {
                received = SavedRecipe(QandA(Query(), it, parseResponse(it)))
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


}