package com.noxapps.dinnerroulette3

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey
import com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
import com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Composable
fun Settings(){
    val context = LocalContext.current
    val store = UserStore(context)
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    var imperial = remember { mutableStateOf(false) }
    var fahrenheit = remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    var allergensOpen by remember { mutableStateOf(false) }

    val allergens = remember { mutableStateListOf<String>() }

    var dd1Expanded by remember { mutableStateOf(false) }
    val skillLevel = listOf("Beginner", "Intermediate", "Expert")
    var skillLevelIndex by remember { mutableStateOf(0) }

    var saveMessage by remember{ mutableStateOf(false) }

    val loadedData = runBlocking { context.dataStore.data.first() }
    loadedData[savedPreferences]?.let { Log.d("saved preferences2", it) }

    loadedData[savedPreferences]?.let{
        val retrievedData = Json.decodeFromString<Settings>(it)
        imperial.value=retrievedData.imperial
        fahrenheit.value=retrievedData.fahrenheit
        skillLevelIndex = retrievedData.skill
        retrievedData.allergens.forEach(){ allergen->
            if(!allergens.contains(allergen))allergens.add(allergen)
        }

    }

    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Use Imperial Units")
            Switch(checked = imperial.value, onCheckedChange = { imperial.value = it })

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Use Fahrenheit")
            Switch(checked = fahrenheit.value, onCheckedChange = { fahrenheit.value = it })
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Alergens / Intolerances")
            Button(onClick = {
                allergensOpen = true
            }) {
                Text(text = "Edit")
            }

        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = "Skill Level:", modifier = Modifier
                .clickable(onClick = {
                    dd1Expanded = true
                })
            )
            Text(
                text = skillLevel[skillLevelIndex],
                textAlign = TextAlign.End, modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        dd1Expanded = true
                    })
            )

            DropdownMenu(
                expanded = dd1Expanded,
                onDismissRequest = { dd1Expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                skillLevel.forEachIndexed() { index, s ->
                    DropdownMenuItem(onClick = {
                        skillLevelIndex = index

                        dd1Expanded = false
                    }, text = { Text(text = s, textAlign = TextAlign.End) },
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }



        }
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                saveMessage=true
                val toSave = Settings(imperial.value, fahrenheit.value, allergens.toList(), skillLevelIndex)
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[savedPreferences] = Json.encodeToString(toSave)
                    }
                }
            }) {
                Text(text = "Save")
            }
        }
    }
    if (saveMessage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("your settings have been saved")
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                saveMessage = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }
                }
            }

        }
    }

    if (allergensOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("Descriptive Tags:")
                        Row(modifier = Modifier
                            .padding(start = 5.dp, end=5.dp)) {
                            Text(text = "")
                            allergens.forEachIndexed() { index, s ->
                                Row() {
                                    Box(modifier = Modifier
                                        .background(ObfsuGrey)
                                        .padding(3.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            allergens.remove(s)
                                        }) {
                                        Row() {
                                            Text(s)
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(1.dp))
                                }
                            }
                        }

                        Row() {
                            val maxChar = 17
                            TextField(
                                value = text,
                                onValueChange = {
                                    if (it.length <= maxChar) text = it
                                },
                                label = { Text("Add Tags") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        allergens.add(text)
                                        text = ""
                                    }
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                allergensOpen = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }
                }
            }

        }
    }

    //save confirmation
}