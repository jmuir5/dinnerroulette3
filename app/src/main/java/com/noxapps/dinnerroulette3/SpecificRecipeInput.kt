package com.noxapps.dinnerroulette3

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SpecificRecipeInput(
    viewModel: InputViewModel = InputViewModel(),
    navController: NavHostController
) {
    DrawerAndScaffold(tabt = "Request Recipe", navController = navController) {
        val focusRequester = remember { FocusRequester() }
        var promptText by remember { mutableStateOf("") }
        var processing = remember { mutableStateOf(false) }
        val placeholder by remember { mutableStateOf(viewModel.randomDishName()) }
        val context = LocalContext.current
        var errorState by remember { mutableStateOf(0) }

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.roulette_table)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap()) }
        val buttonSize = (LocalConfiguration.current.screenWidthDp/2.5).dp

        val stopperFlag by remember { mutableStateOf(false) }

        val primaryOrange = MaterialTheme.colorScheme.primary

        loadInterstitialAd(context, viewModel.mInterstitialAd, viewModel.TAG1, context.getString(R.string.request_interstitial_ad_id))


        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Give me a recipe for:",
                style = MaterialTheme.typography.headlineLarge)
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .drawBehind {
                        val borderSize = 4.dp.toPx()
                        drawLine(
                            color = primaryOrange,
                            start = Offset(12f, size.height),
                            end = Offset(size.width - 12f, size.height),
                            strokeWidth = borderSize
                        )
                    },
                placeholder = { Text(placeholder) },
                value = promptText,
                onValueChange = { if (promptText.length <= 30) promptText = it },
                label = {
                    if (errorState == 1)
                        Text(
                            "Please enter at least 3 characters",
                            color = Color.Red
                        )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (promptText.length < 3) errorState = 1
                        else {
                            errorState = 0
                            viewModel.executeRequest(promptText, processing, context, navController)
                        }
                    }
                )
            )
            Box(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center){
                Image(
                    painter = painter,
                    contentDescription = "DinnerRoulette",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .size(buttonSize),
                    contentScale = ContentScale.Fit
                )
                Button(
                    modifier = Modifier
                        .width(buttonSize)
                        .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height),
                    enabled = !stopperFlag,
                    onClick = {
                        if (promptText.length < 3) errorState = 1
                        else {
                            if (viewModel.mInterstitialAd.value != null) {
                                viewModel.mInterstitialAd.value?.show(context as Activity)
                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                            }
                            errorState = 0
                            viewModel.executeRequest(promptText, processing, context, navController)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text(text = "Generate Recipe",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
        if (processing.value) {
            ProcessingDialog()
        }
    }
}



