package com.noxapps.dinnerroulette3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomePage(
    viewModel: HomeViewModel = HomeViewModel(),
    navController: NavHostController,
) {
    DrawerAndScaffold("Dinner Roulette",navController) {
        var faveList = remember { viewModel.faveFive() }
        var recentList = remember { viewModel.lastFive() }
        val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
        val processing = remember { mutableStateOf(false) }
        val context = LocalContext.current

        val genState = remember { mutableStateOf(true) }
        val savedState = remember { mutableStateOf(false) }
        val faveState = remember { mutableStateOf(false) }
        val recentState = remember { mutableStateOf(false) }



        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                HeadCard(state = genState, title = "Generate New Recipes")
                if (genState.value) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.executeRandom(processing, context, navController)
                        }) {
                        Text(text = "Random New Recipe")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Paths.SpecificRecipeInput.Path)
                        }) {
                        Text(text = "Specific Request")
                    }
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
                        Text(text = "New Input - nyi")
                    }
                    Spacer(modifier = Modifier.size(1.dp))

                    Spacer(modifier = Modifier.size(1.dp))

                }
                //Spacer(modifier = Modifier.size(10.dp))
                Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 1.dp)
                HeadCard(state = savedState, title = "Saved Recipes")
                if (savedState.value) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Paths.Search.Path)
                        }) {
                        Text(text = "Search Recipes - nyi")
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
                    Spacer(modifier = Modifier.size(8.dp))
                    Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 1.dp)
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        items(1) { item ->
                            subHeadCard(state = faveState, title = "Favourites")
                        }
                        if (faveState.value) {
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
                                    Spacer(modifier = Modifier.size(8.dp))
                                }
                            }
                        }
                        items(1) { item ->
                            Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 1.dp)
                            subHeadCard(state = recentState, title = "Recently Generated")
                        }
                        if (recentState.value) {
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
                }
            }

        }
        if (processing.value) {
            ProcessingDialog()
        }
    }

}

@Composable
fun HeadCard(state:MutableState<Boolean>, title:String){
    val height =if (state.value){65.dp}else{100.dp}
    val style = if (state.value){MaterialTheme.typography.titleLarge}else{MaterialTheme.typography.headlineLarge}
    //val color = if (state.value){MaterialTheme.colorScheme.primaryContainer}else{MaterialTheme.colorScheme
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .padding(0.dp, 8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(15.dp)
        )
        .clickable { state.value = !state.value },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge//style
        )
    }
}
@Composable
fun subHeadCard(state:MutableState<Boolean>, title:String){
    val height =if (state.value){45.dp}else{80.dp}
    val style = if (state.value){MaterialTheme.typography.titleLarge} else{MaterialTheme.typography.headlineLarge}
    //val color = if (state.value){MaterialTheme.colorScheme.primaryContainer}else{MaterialTheme.colorScheme
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .padding(8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(15.dp)
        )
        .clickable { state.value = !state.value },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        Text(
            text = title,
            style = style
        )
    }
}