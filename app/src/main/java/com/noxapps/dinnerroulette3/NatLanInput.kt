package com.noxapps.dinnerroulette3

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController


/**
 * unimplemented natural language input for creating recipes. i mainly want it cause i think its
 * a neat idea, but i havent been bothered to actually do any work on it.
 * inspired by beam it and up, the idea is to present the user with an unfinished statement like
 * "i want to make a ... dish" and prompt the user to enter something eg a cuisine, meat etc
 * then use the answers to create the chat gpt question.
 * ill work on it when i get to it i suppose
 */
@Composable
fun NatLanInput(
    viewModel: InputViewModel = InputViewModel(),
    navController: NavHostController
) {
    DrawerAndScaffold(tabt = "NatLan Input", navController = navController) {
        Text("not yet implemented")
    }

}