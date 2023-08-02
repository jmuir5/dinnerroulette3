package com.noxapps.dinnerroulette3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController


@Composable
fun Recipe(recipeId:Long, navController: NavHostController){
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val thisRecipe = recipeBox[recipeId]
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = thisRecipe.title!!)
        Text(text = "Description")
        Text(text = thisRecipe.description!!)
        Text(text = "Ingredients")
        Text(text = thisRecipe.ingredients!!)
        Text(text = "Method")
        Text(text = thisRecipe.method!!)
        Text(text = "Notes")
        Text(text = thisRecipe.notes!!)

    }
}
