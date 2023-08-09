package com.noxapps.dinnerroulette3

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController

@Composable
fun NatLanInput(
    viewModel: InputViewModel = InputViewModel(),
    navController: NavHostController
) {
    DrawerAndScaffold(tabt = "NatLan Input", navController = navController) {
        Text("not yet implemented")
    }

}