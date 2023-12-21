package com.noxapps.dinnerroulette3.commons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.ui.theme.AppTheme
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun PrimaryItemSelector(
    title:String,
    selectedPrimaryIndex: MutableIntState,
    optionsInit: SnapshotStateList<String>,
    currentValue: MutableState<String>,
    icons: Pair<ImageVector, ImageVector>
)   {
    val listPrefix = listOf("Select...", "Any")
    val listSuffix = listOf("Other...","One of...", "All of...","None")
    val options = optionsInit.toMutableList()
    val combinedList = listPrefix+options+listSuffix
    val TITStateList  = remember {
        (0..options.size + 1).map { mutableStateOf(true) }.toMutableList()
    }


    var dropDownExpanded by remember { mutableStateOf(false) }
    val styleRowExpanded = remember {
        derivedStateOf {
            selectedPrimaryIndex.intValue == combinedList.size - 3 ||
                    selectedPrimaryIndex.intValue == combinedList.size - 2
        }
    }
    val otherExpanded =
        remember {
            derivedStateOf {
                selectedPrimaryIndex.intValue == combinedList.size - 4 ||
                        ((selectedPrimaryIndex.value == combinedList.size - 3 || selectedPrimaryIndex.value == combinedList.size - 2) &&
                                TITStateList[TITStateList.size - 1].value)
            }
        }
    var otherText by remember { mutableStateOf("") }
    currentValue.value = derivedStateOf {
        when (selectedPrimaryIndex.intValue) {
            combinedList.size - 1 -> ""
            combinedList.size - 2 -> {
                var returnString = ""
                TITStateList.forEachIndexed { index, it ->
                    if (it.value) {
                        returnString += if (index < options.size) options[index] + ", "
                        else otherText
                    }
                }
                returnString
            }

            combinedList.size - 3 -> {
                var selectedOptions = mutableListOf<String>()
                TITStateList.forEachIndexed { index, it ->
                    if (it.value) {
                        selectedOptions.add(
                            if (index < options.size) options[index]
                            else otherText
                        )
                    }
                }
                if (selectedOptions.size == 0) "None"
                else selectedOptions[abs(Random.nextInt()) % selectedOptions.size]
            }

            combinedList.size - 4 -> otherText

            else -> combinedList[selectedPrimaryIndex.intValue]
        }
    }.value

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clickable(onClick = {
                    dropDownExpanded = true
                })
                .padding(0.dp, 8.dp)
        ) {
            Text(
                text = "What Primary " + title + "?",
                style = MaterialTheme.typography.titleMedium

            )
            DropdownMenu(
                expanded = dropDownExpanded,
                onDismissRequest = { dropDownExpanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 4.dp),
            ) {
                combinedList.forEachIndexed() { index, s ->
                    if (index != 0) {
                        DropdownMenuItem(
                            modifier = Modifier
                                .padding(0.dp, 4.dp)
                                .fillMaxWidth(),
                            onClick = {

                                selectedPrimaryIndex.intValue = index
                                dropDownExpanded = false

                            }, text = {
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
                text = combinedList[selectedPrimaryIndex.intValue],
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        if (styleRowExpanded.value) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                ) {
                    TITLazyRow(
                        values = options + listOf("Other"),
                        states = TITStateList,
                        icons = icons
                    )
                }
                TITControlButtons(TITStateList = TITStateList)
            }
        }
        if (otherExpanded.value) {
            val primary = MaterialTheme.colorScheme.primary
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(0.dp, 8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val borderSize = 4.dp.toPx()
                            drawLine(
                                color = primary,
                                start = Offset(12f, size.height),
                                end = Offset(size.width - 12f, size.height),
                                strokeWidth = borderSize
                            )
                        },
                    placeholder = { Text("Other") },
                    value = otherText,
                    onValueChange = {
                        if (otherText.length <= 60) otherText = it
                        else {
                            if (otherText.length >= it.length) otherText = it
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryItemSelectorPreview() {
    AppTheme {
        val intState = remember { mutableIntStateOf(0) }
        val textState = remember { mutableStateOf("") }
        val exampleMeatItems = remember {
            mutableStateListOf(
                "Beef",
                "Chicken",
                "Pork",
                "Lamb",
                "Shellfish",
                "Salmon",
                "White Fish"
            )
        }
        Column {
            PrimaryItemSelector(
                "example",
                intState,
                exampleMeatItems,
                textState,
                Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)
            )
            Text("Current Valuue: " + textState.value)
        }
    }
}