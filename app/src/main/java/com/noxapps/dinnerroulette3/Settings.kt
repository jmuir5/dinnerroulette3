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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
//import com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey
//import com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
//import com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    TABT: MutableState<String>

) {
    TABT.value = "Settings"
    val context = LocalContext.current
    val store = UserStore(context)
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    var imperial = remember { mutableStateOf(false) }
    var fahrenheit = remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    var allergensOpen = remember { mutableStateOf(false) }

    val allergens = remember { mutableStateListOf<String>() }

    var dd1Expanded by remember { mutableStateOf(false) }
    val skillLevel = listOf("Beginner", "Intermediate", "Expert")
    var skillLevelIndex by remember { mutableStateOf(0) }

    var saveMessage = remember{ mutableStateOf(false) }

    var loadedFlag by remember { mutableStateOf(false)}

    if(!loadedFlag) {
        val loadedData = runBlocking { context.dataStore.data.first() }
        loadedData[savedPreferences]?.let { Log.d("saved preferences2", it) }

        loadedData[savedPreferences]?.let {
            val retrievedData = Json.decodeFromString<Settings>(it)
            imperial.value = retrievedData.imperial
            fahrenheit.value = retrievedData.fahrenheit
            skillLevelIndex = retrievedData.skill
            retrievedData.allergens.forEach() { allergen ->
                if (!allergens.contains(allergen)) allergens.add(allergen)
            }

        }
        loadedFlag=true
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
            Text(text = "Alergens And Intolerances")
            Button(onClick = {
                allergensOpen.value = true
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
                        Log.e("index value1", index.toString())
                        Log.e("index value2", skillLevelIndex.toString())
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
                saveMessage.value=true
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
    if (saveMessage.value) {
        AlertDialog(
            onDismissRequest = {
                saveMessage.value= false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .wrapContentSize(Alignment.Center),
                    //, RoundedCornerShape(15.dp))
                    //.clip(RoundedCornerShape(15.dp))
                    //.border(
                       // width = 1.dp,
                      //  color = PrimaryOrange,
                     //   shape = RoundedCornerShape(15.dp)
                    //),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier= Modifier
                        //.background(SurfaceOrange)
                        .padding(10.dp)

                    ,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Your settings have been saved")
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            saveMessage.value = false
                        }) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }

    if (allergensOpen.value) {
        MultiDialog(allergensOpen, "Allergens And Intolerances", allergens)
    }

    //save confirmation
}