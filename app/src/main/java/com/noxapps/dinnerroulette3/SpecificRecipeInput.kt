package com.noxapps.dinnerroulette3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController

@Composable
fun SpecificRecipeInput(
    viewModel: InputViewModel = InputViewModel(), navController: NavHostController,
    TABT: MutableState<String>

) {
    TABT.value = "Request Recipe"

}