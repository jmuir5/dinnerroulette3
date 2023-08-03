package com.noxapps.dinnerroulette3

import androidx.lifecycle.ViewModel
import io.objectbox.Box
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
        if(allRecipes.size>5) {
            for (i in 0..4) {
                lastFive.add(recipeBox[(allRecipes.size - i - 1).toLong()].id)
            }
        }
        else for(i in allRecipes.size downTo 1){
            lastFive.add(recipeBox[i.toLong()].id)
        }
        return lastFive
    }
}