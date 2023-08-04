package com.noxapps.dinnerroulette3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange

@Composable
fun ErrorPage(
    ErrorBody:String,
    TABT: MutableState<String>

) {
    TABT.value = "An Error Has Occurred"

    Column(modifier = Modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(text = "response from chat GPT:")
        Text(text = ErrorBody)
    }
}