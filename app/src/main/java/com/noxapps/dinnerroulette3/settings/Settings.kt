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
import androidx.compose.runtime.derivedStateOf
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
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.commons.MultiDialog
import com.noxapps.dinnerroulette3.commons.SimpleTextDialogue
import com.noxapps.dinnerroulette3.commons.StyledLazyRow
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.commons.getImageCredits
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
    StandardScaffold(tabt = "Settings", navController = navController, adFlag = false) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var imperial = remember { mutableStateOf(false) }
        var fahrenheit = remember { mutableStateOf(false) }

        var dd1Expanded by remember { mutableStateOf(false) }
        val skillLevel = listOf("Select...","Beginner", "Intermediate", "Expert")
        var skillLevelIndex by remember { mutableStateOf(0) }

        var dietId = remember { mutableStateOf(0L) }

        val budgetOpen = remember { mutableStateOf(false) }
        var budgetIndex by remember {mutableStateOf(0)}
        val budgetItems = listOf("Select...","$","$$","$$$")

        var imageCredits by remember{ mutableStateOf(0)}
        val adState by remember {
            derivedStateOf {
                if(getAdFlag(context))
                    "Enabled"
                else
                    "Disabled"
            }
        }

        var saveMessage = remember { mutableStateOf(false) }

        var loadedFlag by remember { mutableStateOf(false) }

        if (!loadedFlag) {
            val loadedData = runBlocking { context.dataStore.data.first() }

            loadedData[savedPreferences]?.let { Log.d("saved preferences2", it) }

            loadedData[savedPreferences]?.let {
                val retrievedData: SettingsObject = try {
                    Json.decodeFromString<SettingsObject>(it)
                }catch(exception: Exception){
                    SettingsObject(false, false, 0, 0, 0)
                }
                imperial.value = retrievedData.imperial
                fahrenheit.value = retrievedData.fahrenheit
                skillLevelIndex = retrievedData.skill
                dietId.value = retrievedData.dietPreset
                budgetIndex = retrievedData.budget
            }

            imageCredits = getImageCredits(context)
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
                        .padding(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp)

                    ) {
                        Text(
                            text = "Shop:",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp)

                    ) {
                        Text(
                            text = "Ad Status:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text =  adState,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp)

                    ) {
                        Text(
                            text = "Current Image Credits:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text =  imageCredits.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Button(onClick = {
                            navController.navigate(Paths.Billing.Path)
                        }) {
                            Text(
                                text =  "Go To Shop"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp)

                    ) {
                        Text(
                            text = "Settings:",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
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
                        Switch(checked = imperial.value,
                            onCheckedChange = { imperial.value = it }
                        )

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
                            onCheckedChange = { fahrenheit.value = it }
                        )
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
                                                style = MaterialTheme.typography.titleMedium,
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
                            style = MaterialTheme.typography.titleMedium,
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
                            }, text = { Text(text = s,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.End) },
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
                                skillLevelIndex,
                                dietId.value,
                                budgetIndex
                            )
                            scope.launch {
                                context.dataStore.edit { settings ->
                                    settings[savedPreferences] = Json.encodeToString(toSave)
                                }
                            }
                        }) {
                            Text(text = "Save Settings",
                                style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = "Promo Codes:",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Button(onClick = {
                            navController.navigate(Paths.Redeem.Path)
                        }) {
                            Text(
                                text =  "Redeem Code"
                            )
                        }
                    }

                }
            }
            if (saveMessage.value) {
                SimpleTextDialogue(title = null, body = "Your Settings Have Been Saved", saveMessage)
            }
        }
    }
}