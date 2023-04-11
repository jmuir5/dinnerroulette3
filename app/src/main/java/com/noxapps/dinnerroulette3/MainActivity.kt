package com.noxapps.dinnerroulette3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Popup
import com.noxapps.dinnerroulette3.ui.theme.DinnerRoulette3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DinnerRoulette3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    optionsInput()
                }
            }
        }
    }
}

@Composable
fun optionsInput() {
    var dd1Expanded by remember{ mutableStateOf(false) }
    var dd2Expanded by remember{ mutableStateOf(false) }
    var dd3Expanded by remember{ mutableStateOf(false) }
    var dd4Expanded by remember{ mutableStateOf(false) }
    var dd5Expanded by remember{ mutableStateOf(false) }

    var meatExpanded by remember{ mutableStateOf(false) }
    var carbExpanded by remember{ mutableStateOf(false) }
    var additionalExpanded by remember{ mutableStateOf(false)}

    val meatContentItems = listOf("Select...", "Yes", "Optional", "Vegetarian", "Vegan")
    val primaryMeatItems = listOf("Select...", "Any", "Beef", "Chicken", "Pork", "Lamb", "Shellfish", "Salmon", "White Fish" )
    val primaryCarbItems = listOf("Select...", "Any", "Pasta", "Potato", "Rice", "Other", "None")
    val triStateItems = listOf("Optional", "Yes", "No")

    val ingredientsList = mutableListOf<String>("test1","test2")
    val tagsList = mutableListOf<String>()
    var text by remember{ mutableStateOf("") }

    var meatContentIndex by remember{ mutableStateOf(0) }
    var primaryMeatIndex by remember{ mutableStateOf(0) }
    var primaryCarbIndex by remember{ mutableStateOf(0) }
    var spiceIndex by remember{ mutableStateOf(0) }
    var cheeseIndex by remember{ mutableStateOf(0) }

    var glutenChecked by remember{ mutableStateOf(false) }
    var lactoseChecked by remember{ mutableStateOf(false) }

    var ingredientsOpen by remember{mutableStateOf(false)}
    var tagsOpen by remember{mutableStateOf(false)}


    Box(modifier= Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)) {
        Column() {
            Text(text = "Dinner Roulette")
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Meat Content:")
                Text(text= meatContentItems[meatContentIndex],
                    textAlign = TextAlign.End, modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            dd1Expanded = true
                        })
                )

                DropdownMenu(
                    expanded = dd1Expanded,
                    onDismissRequest = { dd1Expanded=false },
                    modifier=Modifier.fillMaxWidth()
                    ) {
                    meatContentItems.forEachIndexed(){ index, s->
                        if(index!=0) {
                            DropdownMenuItem(onClick = {
                                meatExpanded = index==1||index==2
                                if(!carbExpanded) carbExpanded=index==3||index==4
                                meatContentIndex = index
                                dd1Expanded = false
                            }, text = { Text(text = s, textAlign = TextAlign.End) })
                        }
                    }
                }

            }
            if(meatExpanded){
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
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
                            if(index!=0) {
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
            if(carbExpanded){
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
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
                            if(index!=0) {
                                DropdownMenuItem(onClick = {
                                    additionalExpanded=true
                                    primaryCarbIndex = index
                                    dd3Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })
                            }
                        }
                    }
                }
            }
            if(additionalExpanded){
                Column(){
                    Row(horizontalArrangement = Arrangement.SpaceBetween){
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
                    Row(horizontalArrangement = Arrangement.SpaceBetween){
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
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = "Gluten Free:")
                        Switch(checked = glutenChecked, onCheckedChange = { glutenChecked=it})

                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = "Lactose Free:")
                        Switch(checked = lactoseChecked, onCheckedChange = { lactoseChecked=it})
                        
                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = "Additional Ingredients:")
                        Button(onClick = {
                            ingredientsOpen=true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = "Descriptive Tags:")
                        Button(onClick = {
                            tagsOpen=true
                        }) {
                            Text(text = "Edit")
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
                .wrapContentSize(Alignment.TopStart)
        ) {Column() {
            Text("Additional Ingredients:")
            Row() {
                ingredientsList.forEachIndexed() { index, s ->
                    ClickableText(text = AnnotatedString(s), onClick={
                        ingredientsList.removeAt(index)
                        Log.e("ingredient list", ingredientsList.toString())
                    })
                }
            }

            Row(){
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("add ingredients") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            ingredientsList+=text
                            Log.e("ingredient list", ingredientsList.toString())
                            ingredientsOpen=false
                            text=""
                        }
                    )
                )
            }
            Button(onClick = {
                ingredientsOpen=false
                text=""
            }) {
                Text(text = "Retrun")
            }

        }

        }
    }

    if (tagsOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
        ) {
            Column() {
                Text("Descriptive Tags:")
                Row() {
                    tagsList.forEachIndexed() { index, s ->
                        ClickableText(text = AnnotatedString(s), onClick={tagsList.removeAt(index)})
                    }
                }

                Row(){
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("add ingredients") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                tagsList.add(text)
                                Log.e("tags list", tagsList.toString())
                                text=""
                            }
                        )
                    )
                }
                Button(onClick = {
                    tagsOpen=false
                    text=""
                }) {
                    Text(text = "Retrun")
                }

            }
        }
    }
}


@Composable
fun ingredientsinput(ingredientsList:MutableList<String>,  text:String, ingredientsOpen:Boolean){
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DinnerRoulette3Theme {
        optionsInput()
    }
}