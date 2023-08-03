package com.noxapps.dinnerroulette3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun SearchPage(TABT: MutableState<String>
) {
    TABT.value = "Search"
}