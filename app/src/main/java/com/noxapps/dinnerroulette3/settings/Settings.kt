package com.noxapps.dinnerroulette3.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.DrawerAndScaffold
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.input.MultiDialog
import com.noxapps.dinnerroulette3.input.SettingsObject
import com.noxapps.dinnerroulette3.input.StyledLazyRow
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
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
    navController: NavHostController
) {
    DrawerAndScaffold(tabt = "Settings", navController = navController) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var imperial = remember { mutableStateOf(false) }
        var fahrenheit = remember { mutableStateOf(false) }

        var allergensOpen = remember { mutableStateOf(false) }
        val allergens = remember { mutableStateListOf<String>() }

        var dd1Expanded by remember { mutableStateOf(false) }
        val skillLevel = listOf("Beginner", "Intermediate", "Expert")
        var skillLevelIndex by remember { mutableStateOf(0) }

        var dd2Expanded by remember{mutableStateOf(false)}
        var meatContent = listOf("Optional", "Always","Vegitarian", "Vegan")
        var meatContentIndex by remember { mutableStateOf(0) }

        var dietId = remember { mutableStateOf(0L) }

        val budgetOpen = remember { mutableStateOf(false) }
        var budgetIndex by remember {mutableStateOf(0)}
        val budgetItems = listOf("Select...","$","$$","$$$")

        var imageCredits by remember{ mutableStateOf(0)}

        var saveMessage = remember { mutableStateOf(false) }

        var loadedFlag by remember { mutableStateOf(false) }

        if (!loadedFlag) {
            val loadedData = runBlocking { context.dataStore.data.first() }

            loadedData[savedPreferences]?.let { Log.d("saved preferences2", it) }

            loadedData[savedPreferences]?.let {
                val retrievedData: SettingsObject = try {
                    Json.decodeFromString<SettingsObject>(it)
                }catch(exception: Exception){
                    SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
                }
                imperial.value = retrievedData.imperial
                fahrenheit.value = retrievedData.fahrenheit
                skillLevelIndex = retrievedData.skill
                retrievedData.allergens.forEach() { allergen ->
                    if (!allergens.contains(allergen)) allergens.add(allergen)
                }
                dietId.value = retrievedData.dietPreset
                meatContentIndex = retrievedData.meatContent
                budgetIndex = retrievedData.budget
                imageCredits = retrievedData.imageCredits

            }
            loadedFlag = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
        ) {

            Column {
                Column(
                    modifier = Modifier
                        .padding(24.dp, 24.dp, 24.dp, 0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clickable(onClick = {
                                dd2Expanded = true
                            })
                            .padding(0.dp, 8.dp)
                    ) {
                        Text(
                            text = "Meat Content Preset",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = meatContent[meatContentIndex],
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    DropdownMenu(
                        expanded = dd2Expanded,
                        onDismissRequest = { dd2Expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        meatContent.forEachIndexed() { index, s ->
                            DropdownMenuItem(onClick = {
                                meatContentIndex = index
                                dd2Expanded = false
                            }, text = { Text(text = s, textAlign = TextAlign.End) },
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = {
                                navController.navigate(Paths.DietPreset.Path)
                            })
                            .padding(0.dp, 8.dp)
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = "Diet Preset",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val presetLabel = if(dietId.value==0L){
                            "Set Preset"
                        } else
                            ObjectBox.store.boxFor(DietPreset::class.java)[dietId.value].name
                        Text(
                            text = presetLabel,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        /*
                        Button(onClick = {
                            navController.navigate(Paths.DietPreset.Path)
                        }) {
                            Text(
                                text =  "->" 
                            )
                        }
                         */

                    }
                    Row(                                                                //Budget
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clickable(onClick = {
                                budgetOpen.value = true
                            })
                            .padding(0.dp, 8.dp)
                    ) {
                        Text(
                            text = "Budget:",
                            style = MaterialTheme.typography.titleMedium

                        )
                        DropdownMenu(
                            expanded = budgetOpen.value,
                            onDismissRequest = { budgetOpen.value = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp, 4.dp),
                        ) {
                            budgetItems.forEachIndexed() { index, s ->
                                if (index != 0) {
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .padding(0.dp, 4.dp)
                                            .fillMaxWidth(),
                                        onClick = {
                                            budgetIndex = index
                                            budgetOpen.value = false
                                        },
                                        text = {
                                            Text(
                                                text = s,
                                                textAlign = TextAlign.End,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        Text(
                            text = budgetItems[budgetIndex],
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Use Imperial Units?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(checked = imperial.value, onCheckedChange = { imperial.value = it })

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Use Fahrenheit?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = fahrenheit.value,
                            onCheckedChange = { fahrenheit.value = it })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Allergens And Intolerances:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = {
                            allergensOpen.value = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                }
                StyledLazyRow(array = allergens, false, 24.dp)
                Column(modifier = Modifier
                    .padding(24.dp, 0.dp, 24.dp, 0.dp)){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clickable(onClick = {
                                dd1Expanded = true
                            })
                            .padding(0.dp, 8.dp)
                    ) {
                        Text(
                            text = "Skill Level:",
                            style = MaterialTheme.typography.titleMedium

                        )
                        Text(
                            text = skillLevel[skillLevelIndex],
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            saveMessage.value = true
                            val toSave = SettingsObject(
                                imperial.value,
                                fahrenheit.value,
                                allergens.toList(),
                                skillLevelIndex,
                                dietId.value,
                                meatContentIndex,
                                budgetIndex,
                                imageCredits
                            )
                            scope.launch {
                                context.dataStore.edit { settings ->
                                    settings[savedPreferences] = Json.encodeToString(toSave)
                                }
                            }
                        }) {
                            Text(text = "Save")
                        }
                    }
                    Spacer(modifier = Modifier.size(48.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = "Current Image Credits:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text =  imageCredits.toString()
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Button(onClick = {
                            navController.navigate(Paths.Redeem.Path)
                        }) {
                            Text(
                                text =  "Purchase Image Credits"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(48.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = "Redeem Code",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = {
                            navController.navigate(Paths.Redeem.Path)
                        }) {
                            Text(
                                text =  "->"
                            )
                        }
                    }

                }
            }
            if (saveMessage.value) {
                AlertDialog(
                    onDismissRequest = {
                        saveMessage.value = false
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .wrapContentSize(Alignment.Center),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(
                            modifier = Modifier
                                //.background(SurfaceOrange)
                                .padding(10.dp),
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
                MultiDialog(allergensOpen, "Allergens And Intolerances", "Add", allergens)
            }
        }
    }
}