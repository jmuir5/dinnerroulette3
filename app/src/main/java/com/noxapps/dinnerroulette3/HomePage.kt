package com.noxapps.dinnerroulette3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomePage(
    viewModel: HomeViewModel = HomeViewModel(), navController: NavHostController,
    TABT: MutableState<String>

) {
    TABT.value = "Dinner Roulette"
    var faveList = remember {viewModel.randomFave()}
    var recentList = remember{viewModel.lastFive()}
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)


    Column(
        modifier=Modifier.padding(horizontal=8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Dinner Roulette")
        Text("Generate new recipe")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            navController.navigate(Paths.NewInput.Path)
        }) {
            Text(text = "Classic Input")
        }
        Spacer(modifier = Modifier.size(1.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            navController.navigate(Paths.NatLanInput.Path)
        }) {
            Text(text = "New Input")
        }
        Spacer(modifier = Modifier.size(1.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            navController.navigate(Paths.SpecificRecipeInput.Path)
        }) {
            Text(text = "Specific Request")
        }
        Spacer(modifier = Modifier.size(1.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                //navController.navigate(Paths.SpecificRecipeInput.Path)
            }) {
            Text(text = "Random New Recipe")
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Saved Recipes")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            navController.navigate(Paths.Search.Path)
        }) {
            Text(text = "Search Recipes")
        }

        Spacer(modifier = Modifier.size(1.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                //navController.navigate(Paths.SpecificRecipeInput.Path)
            }) {
            Text(text = "Random Saved Recipe")
        }
        Text(text = "Favourites")

        if(faveList.isEmpty()){
            Text("No favourite recipes. Make some recipes you love!")
        }
        else {
            faveList.forEach {
                Spacer(modifier = Modifier.size(1.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    navController.navigate(Paths.Recipe.Path + "/" + it)
                }) {
                    recipeBox[it].title?.let { it1 -> Text(text = it1) }
                }
            }
        }
        Text(text = "Recently Generated")
        if(recentList.isEmpty()){
            Text("No recent recipes. Make something new!")
        }
        else {
            recentList.forEach {
                Spacer(modifier = Modifier.size(1.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    navController.navigate(Paths.Recipe.Path + "/" + it)
                }) {
                    recipeBox[it].title?.let { it1 -> Text(text = it1) }
                }
            }
        }
    }
}