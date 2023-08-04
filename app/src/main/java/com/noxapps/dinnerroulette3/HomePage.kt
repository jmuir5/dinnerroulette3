package com.noxapps.dinnerroulette3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val processing = remember{mutableStateOf(false)}
    val context = LocalContext.current



    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    viewModel.executeRandom(processing, context, navController)
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
                    navController.navigate(Paths.Recipe.Path + "/" + viewModel.randomFavourite())
                }) {
                Text(text = "Random Favourite Recipe")
            }
            Spacer(modifier = Modifier.size(1.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(Paths.Recipe.Path + "/" + viewModel.randomSaved())
                }) {
                Text(text = "Random Saved Recipe")
            }
            Text(text = "Favourites")
        }
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            if (faveList.isEmpty()) {
                items(1) { item ->
                    Text("No favourite recipes. Make some recipes you love!")
                }
            } else {
                items(faveList.size) {
                    Spacer(modifier = Modifier.size(1.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Paths.Recipe.Path + "/" + faveList[it])
                        }) {
                        recipeBox[faveList[it]].title?.let { it1 -> Text(text = it1) }
                    }
                }
            }
            items(1) { item ->
                Text(text = "Recently Generated")
            }

            if (recentList.isEmpty()) {
                items(1) { item ->
                    Text("No recent recipes. Make something new!")
                }
            } else {
                items(recentList.size) {
                    Spacer(modifier = Modifier.size(1.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Paths.Recipe.Path + "/" + recentList[it])
                        }) {
                        recipeBox[recentList[it]].title?.let { it1 -> Text(text = it1) }
                    }
                }
            }
        }
    }
    if (processing.value) {
        ProcessingDialog()
    }

}