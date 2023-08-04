package com.noxapps.dinnerroulette3

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SpecificRecipeInput(
    viewModel: InputViewModel = InputViewModel(), navController: NavHostController,
    TABT: MutableState<String>

) {
    TABT.value = "Request Recipe"
    val focusRequester = remember { FocusRequester() }
    var promptText by  remember { mutableStateOf("") }
    var processing = remember{mutableStateOf(false)}
    val placeholder by remember {mutableStateOf(viewModel.randomDishName())}
    val context = LocalContext.current
    Column(
        Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Give me a recipe for:")
        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            placeholder = {Text(placeholder)},
            value = promptText,
            onValueChange = { if(promptText.length<=30)promptText = it },
            label = {},
            colors = OutlinedTextFieldDefaults.colors(
                //focusedBorderColor = SurfaceOrange,
                //unfocusedBorderColor = SurfaceOrange
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.executeRequest(promptText, processing, context, navController)
                }
            )
        )

        Button(onClick = {
            viewModel.executeRequest(promptText, processing, context, navController)
        }) {
            Text(text = "Generate Recipe")
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
    if(processing.value){
        ProcessingDialog()
    }

}



