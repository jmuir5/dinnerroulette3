package com.noxapps.dinnerroulette3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.ui.theme.DinnerRoulette3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DinnerRoulette3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange//MaterialTheme.colorScheme.background
                ) {
                    OptionsInput()
                }
            }
        }
    }
}

@Composable
fun OptionsInput(
    viewModel: optionsViewModel = optionsViewModel(),
) {
    var dd1Expanded by remember { mutableStateOf(false) }
    var dd2Expanded by remember { mutableStateOf(false) }
    var dd3Expanded by remember { mutableStateOf(false) }
    var dd4Expanded by remember { mutableStateOf(false) }
    var dd5Expanded by remember { mutableStateOf(false) }

    var meatExpanded by remember { mutableStateOf(false) }
    var carbExpanded by remember { mutableStateOf(false) }
    var additionalExpanded by remember { mutableStateOf(true) }

    val meatContentItems = listOf("Select...", "Yes", "Optional", "Vegetarian", "Vegan")
    val primaryMeatItems = listOf(
        "Select...",
        "Any",
        "Beef",
        "Chicken",
        "Pork",
        "Lamb",
        "Shellfish",
        "Salmon",
        "White Fish"
    )
    val primaryCarbItems = listOf("Select...", "Any", "Pasta", "Potato", "Rice", "Bread", "Other", "None")
    val triStateItems = listOf("Optional", "Yes", "No")

    var text by remember { mutableStateOf("") }

    val ingredients = remember { mutableStateListOf<String>() }
    val tags = remember { mutableStateListOf<String>() }

    var meatContentIndex by remember { mutableStateOf(0) }
    var primaryMeatIndex by remember { mutableStateOf(0) }
    var primaryCarbIndex by remember { mutableStateOf(0) }
    var spiceIndex by remember { mutableStateOf(0) }
    var cheeseIndex by remember { mutableStateOf(0) }

    var glutenChecked by remember { mutableStateOf(false) }
    var lactoseChecked by remember { mutableStateOf(false) }

    var ingredientsOpen by remember { mutableStateOf(false) }
    var tagsOpen by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Column() {
            Text(text = "Dinner Roulette")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = "Meat Content:")
                Text(
                    text = meatContentItems[meatContentIndex],
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
                    meatContentItems.forEachIndexed() { index, s ->
                        if (index != 0) {
                            DropdownMenuItem(onClick = {
                                meatExpanded = index == 1 || index == 2
                                if (!carbExpanded) carbExpanded = index == 3 || index == 4
                                meatContentIndex = index

                                dd1Expanded = false
                            }, text = { Text(text = s, textAlign = TextAlign.End) },
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }

            }
            if (meatExpanded) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(text = "Primary Meat:")
                    Text(
                        text = primaryMeatItems[primaryMeatIndex],
                        textAlign = TextAlign.End, modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                dd2Expanded = true
                            })
                    )

                    DropdownMenu(
                        expanded = dd2Expanded,
                        onDismissRequest = { dd2Expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        primaryMeatItems.forEachIndexed() { index, s ->
                            if (index != 0) {
                                DropdownMenuItem(onClick = {
                                    carbExpanded = true
                                    primaryMeatIndex = index
                                    dd2Expanded = false

                                }, text = { Text(text = s, textAlign = TextAlign.End) })
                            }
                        }
                    }
                }
            }
            if (carbExpanded) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(text = "Primary Carbohydrate:")
                    Text(
                        text = primaryCarbItems[primaryCarbIndex],
                        textAlign = TextAlign.End, modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                dd3Expanded = true
                            })
                    )

                    DropdownMenu(
                        expanded = dd3Expanded,
                        onDismissRequest = { dd3Expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        primaryCarbItems.forEachIndexed() { index, s ->
                            if (index != 0) {
                                DropdownMenuItem(onClick = {
                                    additionalExpanded = true
                                    primaryCarbIndex = index
                                    dd3Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })
                            }
                        }
                    }
                }
            }
            if (additionalExpanded) {
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(text = "Spice Content:")
                        Text(
                            text = triStateItems[spiceIndex],
                            textAlign = TextAlign.End, modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    dd4Expanded = true
                                })
                        )

                        DropdownMenu(
                            expanded = dd4Expanded,
                            onDismissRequest = { dd4Expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            triStateItems.forEachIndexed() { index, s ->
                                DropdownMenuItem(onClick = {
                                    spiceIndex = index
                                    dd4Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })

                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(text = "Cheese Content:")
                        Text(
                            text = triStateItems[cheeseIndex],
                            textAlign = TextAlign.End, modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    dd5Expanded = true
                                })
                        )

                        DropdownMenu(
                            expanded = dd5Expanded,
                            onDismissRequest = { dd5Expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            triStateItems.forEachIndexed() { index, s ->
                                DropdownMenuItem(onClick = {
                                    cheeseIndex = index
                                    dd5Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })

                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Gluten Free:")
                        Switch(checked = glutenChecked, onCheckedChange = { glutenChecked = it })

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Lactose Free:")
                        Switch(checked = lactoseChecked, onCheckedChange = { lactoseChecked = it })

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Additional Ingredients:")
                        Button(onClick = {
                            ingredientsOpen = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Descriptive Tags:")
                        Button(onClick = {
                            tagsOpen = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(onClick = {
                            val query = Query(meatContentItems[meatContentIndex],
                                primaryMeatItems[primaryMeatIndex],
                                primaryCarbItems[primaryCarbIndex],
                                spiceIndex,
                                cheeseIndex,
                                glutenChecked,
                                lactoseChecked,
                                ingredients,
                                tags)
                            var question2=viewModel.generateQuestion(query)
                            Log.d("constructed question", question2)
                            //viewModel.getResponse(question2)

                        }) {
                            Text(text = "Generate Recipe")
                        }

                    }
                }
            }


        }
    }
    if (ingredientsOpen) {
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
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
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
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
                ) {
                Column() {
                    Text("Additional Ingredients:",)
                    Row() {
                        Text(text = "")
                        ingredients.forEachIndexed() { index, s ->
                            ClickableText(text = AnnotatedString(s), onClick = {
                                ingredients.remove(s)
                            })

                        }
                    }

                    Row() {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("add ingredients") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    ingredients.add(text)
                                    text = ""
                                }
                            )
                        )
                    }
                    Row(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        Button(onClick = {
                            ingredientsOpen = false
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

    if (tagsOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {}
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)

                    .wrapContentSize(Alignment.Center)
                    .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                    .clip(RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))

            ) {
                Column() {
                    Text("Descriptive Tags:")
                    Row() {
                        Text(text = "")
                        tags.forEachIndexed() { index, s ->
                            ClickableText(text = AnnotatedString(s), onClick = { tags.remove(s) })
                        }
                    }

                    Row() {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Add Tags") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    tags.add(text)
                                    //Log.e("tags list", tagsList.toString())
                                    text = ""
                                }
                            )
                        )
                    }
                    Row(modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        Button(onClick = {
                            tagsOpen = false
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



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DinnerRoulette3Theme {
        OptionsInput()
    }
}