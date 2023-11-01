package com.noxapps.dinnerroulette3.input

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.InterstitialAdDialogue
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.commons.DietSelectDialog
import com.noxapps.dinnerroulette3.commons.ProcessingDialog
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.loadInterstitialAd
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.settings.SettingsObject
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun SpecificRecipeInput(
    viewModel: InputViewModel = InputViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    val adFlag = getAdFlag(context)

    StandardScaffold(tabt = "Create Custom Recipe", navController = navController, adFlag = adFlag) {
        val focusRequester = remember { FocusRequester() }
        var promptText by remember { mutableStateOf("") }
        var processing = remember { mutableStateOf(false) }
        val placeholder by remember { mutableStateOf(viewModel.randomDishName()) }
        var errorState by remember { mutableStateOf(0) }

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.roulette_table)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap()) }
        val buttonSize = (LocalConfiguration.current.screenWidthDp/2.5).dp

        val presetId = remember{mutableStateOf(0L)}
        val presetSelectFlag = remember { mutableStateOf(false) }
        var loadedFlag by remember { mutableStateOf(false) }

        if (!loadedFlag) {
            val loadedData = runBlocking { context.dataStore.data.first() }
            loadedData[savedPreferences]?.let {
                val retrievedData = try {
                    Json.decodeFromString<SettingsObject>(it)
                } catch (exception: Exception) {
                    SettingsObject(false, false, 0, 0, 0)
                }

                presetId.value = retrievedData.dietPreset
            }
            loadedFlag = true
        }


        val primaryOrange = MaterialTheme.colorScheme.primary

        val adFrameFlag = remember { mutableStateOf(false) }
        val adReference = if(BuildConfig.DEBUG){
            LocalContext.current.getString(R.string.test_roulette_interstitial_ad_id)
        }
        else LocalContext.current.getString(R.string.roulette_interstitial_ad_id)

        loadInterstitialAd(context, viewModel.mInterstitialAd, viewModel.TAG1, adReference)


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
                onValueChange = {
                    if (promptText.length <= 60) promptText = it
                    else {
                        if (promptText.length >= it.length) promptText = it
                    }
                },
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
                            if(viewModel.recipeBox.all.size<2 ||!adFlag){
                                viewModel.executeRequest(promptText, processing,  presetId.value, context, navController)
                            }
                            else {
                                adFrameFlag.value = true
                            }
                        }
                    }
                )
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 10.dp)
                    .clickable(onClick = {
                        presetSelectFlag.value=true
                    })

            ) {
                Text(
                    text = "Active Preset:",
                    style = MaterialTheme.typography.titleMedium
                )
                val presetLabel = if(presetId.value==0L){
                    "No Preset"
                } else
                    ObjectBox.store.boxFor(DietPreset::class.java)[presetId.value].name
                Text(
                    text = presetLabel,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Box(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center){
                Image(
                    painter = painter,
                    contentDescription = "DinnerRoulette",
                    modifier = Modifier
                        //.fillMaxWidth()
                        .size(buttonSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
                Button(
                    modifier = Modifier
                        .width(buttonSize)
                        .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height),
                    onClick = {
                        if (promptText.length < 3) errorState = 1
                        else {
                            errorState = 0
                            if(viewModel.recipeBox.all.size<2 ||!adFlag){
                                viewModel.executeRequest(promptText, processing, presetId.value, context, navController)
                            }
                            else {
                                adFrameFlag.value = true
                            }
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
            ProcessingDialog("Currently generating your recipe for $promptText")
        }
        if(presetSelectFlag.value){
            DietSelectDialog(
                stateValue = presetSelectFlag,
                options = viewModel.presetBox.all,
                title = "SelectPreset",
                selected = presetId,
                resetFlag = remember{ mutableStateOf(false) },
                editableFlag = false
            )
        }
        if(adFrameFlag.value){
            InterstitialAdDialogue(
                mInterstitialAd = viewModel.mInterstitialAd,
                context = context,
                displayFlag = adFrameFlag,
                function = {
                    viewModel.executeRequest(promptText, processing,  presetId.value,context, navController)
                }
            )
        }
    }
}



