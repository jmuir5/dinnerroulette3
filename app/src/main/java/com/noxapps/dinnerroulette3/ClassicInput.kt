package com.noxapps.dinnerroulette3

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey
import com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
import com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewInput(
    viewModel: InputViewModel = InputViewModel(), navController: NavHostController,
    TABT: MutableState<String>
) {
    TABT.value="Create Recipe"
    var dd1Expanded by remember { mutableStateOf(false) }
    var dd2Expanded by remember { mutableStateOf(false) }
    var dd3Expanded by remember { mutableStateOf(false) }


    var meatExpanded by remember { mutableStateOf(false) }
    var carbExpanded by remember { mutableStateOf(false) }
    var additionalExpanded by remember { mutableStateOf(false) }

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
    val primaryCarbItems =
        listOf("Select...", "Any", "Pasta", "Potato", "Rice", "Bread", "Other", "None")


    var text by remember { mutableStateOf("") }

    var cuisineText = remember { mutableStateOf("(Optional)") }

    val ingredients = remember { mutableStateListOf<String>() }
    val exclIngredients = remember { mutableStateListOf<String>() }
    val tags = remember { mutableStateListOf<String>() }


    var meatContentIndex by remember { mutableStateOf(0) }
    var primaryMeatIndex by remember { mutableStateOf(0) }
    var primaryCarbIndex by remember { mutableStateOf(0) }


    var cuisine = remember { mutableStateOf(false) }
    var addIngredientsOpen = remember { mutableStateOf(false) }
    var removeIngredientsOpen = remember { mutableStateOf(false) }
    var tagsOpen = remember { mutableStateOf(false) }
    var processing = remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val store = UserStore(context)
    val loadedData = runBlocking { context.dataStore.data.first() }


    var stopper by remember { mutableStateOf(false) }
    var stopperFlag by remember{ mutableStateOf(false)}
    var disclamer by remember { mutableStateOf(true) }
    loadedData[usedTokens]?.let { Log.d("tokens used", it.toString()) }
    loadedData[usedTokens]?.let {
        if (it > 5000) {
            stopper = true
            stopperFlag = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = "Meat Content:", modifier = Modifier
                        .clickable(onClick = {
                            dd1Expanded = true
                        })
                )
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
                    Text(
                        text = "Primary Meat:", modifier = Modifier
                            .clickable(onClick = {
                                dd2Expanded = true
                            })
                    )
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
                    Text(
                        text = "Primary Carbohydrate:", modifier = Modifier
                            .clickable(onClick = {
                                dd3Expanded = true
                            })
                    )
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
                        Text(
                            text = "Cuisine:", modifier = Modifier
                                .clickable(onClick = {
                                    cuisine.value = true
                                })
                        )
                        Text(
                            text = cuisineText.value,
                            textAlign = TextAlign.End, modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    cuisine.value = true
                                })
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add Ingredients:")
                        Button(onClick = {
                            addIngredientsOpen.value = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Excluded Ingredients:")
                        Button(onClick = {
                            removeIngredientsOpen.value = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Descriptive Tags:")
                        Button(onClick = {
                            tagsOpen.value = true
                        }) {
                            Text(text = "Edit")
                        }

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(!stopperFlag) {
                            Button(onClick = {
                                val query = Query(
                                    meatContentItems[meatContentIndex],
                                    primaryMeatItems[primaryMeatIndex],
                                    primaryCarbItems[primaryCarbIndex],
                                    cuisineText.value,
                                    ingredients,
                                    exclIngredients,
                                    tags
                                )
                                viewModel.executeClassic(query, processing, context, navController)


                            }) {
                                Text(text = "Generate Recipe")
                            }
                        }
                        else{
                            Text(text = "All Beta Tokens Used")
                        }

                    }

                }
            }
        }
    }

    if (cuisine.value) {
        SingleDialog(cuisine, "Include Ingredients", cuisineText)
    }

    if (addIngredientsOpen.value) {
        MultiDialog(addIngredientsOpen, "Include Ingredients", ingredients)
    }

    if (removeIngredientsOpen.value) {
        MultiDialog(removeIngredientsOpen, "Exclude Ingredients", exclIngredients)
    }

    if (tagsOpen.value) {
        MultiDialog(tagsOpen, "Descriptive Tags", tags)
    }

    if (processing.value) {
        ProcessingDialog()
    }

    if (stopper) {
        AlertDialog(
            onDismissRequest = {
                stopper = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .wrapContentSize(Alignment.Center)
                    //, RoundedCornerShape(15.dp))
                    //.clip(RoundedCornerShape(15.dp))
                    .border(
                        width = 1.dp,
                        color = PrimaryOrange,
                        shape = RoundedCornerShape(15.dp)
                    ),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier= Modifier
                        .background(SurfaceOrange)
                        .padding(10.dp)

                    ,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("max tokens used")
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "This is an early beta build with limited recipe genration. \n" +
                                    "You will be unable to generate any more recipes.\n" +
                                    "Please contact Chris to generate more recipes."
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            stopper = false
                            text = ""
                        }) {
                            Text(text = "I Understand")
                        }
                    }
                }
            }
        }
    }

    if (disclamer) {
        AlertDialog(
            onDismissRequest = {
                disclamer = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .wrapContentSize(Alignment.Center)
                    //, RoundedCornerShape(15.dp))
                    //.clip(RoundedCornerShape(15.dp))
                    .border(
                        width = 1.dp,
                        color = PrimaryOrange,
                        shape = RoundedCornerShape(15.dp)
                    ),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .background(SurfaceOrange)
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("DISCLAIMER:")
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "This is an early beta build of this application. There will be bugs" +
                                    "and stuff that's not very easy to use. Please don't hesitate to contact " +
                                    "me to give me feedback. \nThis version is limited to roughly 10 recipe " +
                                    "generations. You can continue to view recipes after that runs out but " +
                                    "old recipes may not be carried over to new versions of the app. \n" +
                                    "All recipes are generated by chat gpt, so please use descretion when" +
                                    " actually cooking them, especially if you have intolerances or alergies." +
                                    "The settings page has a place for you to add intollerences and alergies, " +
                                    "please test this if applicable."
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            disclamer = false
                            text = ""
                        }) {
                            Text(text = "I Understand")
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    array: SnapshotStateList<String>
) {
    val interactionSource = remember { MutableInteractionSource() }
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            stateValue.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .wrapContentSize(Alignment.Center)
                //, RoundedCornerShape(15.dp))
                //.clip(RoundedCornerShape(15.dp))
                .border(
                    width = 1.dp,
                    color = PrimaryOrange,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier= Modifier
                    .background(SurfaceOrange)
                    .padding(10.dp)

                    ,
            ) {
                Text(title)
                Row() {
                    Text(text = "")
                    array.forEachIndexed() { index, s ->
                        Row() {
                            Box(modifier = Modifier
                                .background(ObfsuGrey)
                                .padding(3.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    array.remove(s)
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
                        label = { Text("add ingredients") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                array.add(text)
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
                        stateValue.value = false
                        text = ""
                    }) {
                        Text(text = "Retrun")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    data: MutableState<String>
) {
    val interactionSource = remember { MutableInteractionSource() }
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            stateValue.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .wrapContentSize(Alignment.Center)
                //, RoundedCornerShape(15.dp))
                //.clip(RoundedCornerShape(15.dp))
                .border(
                    width = 1.dp,
                    color = PrimaryOrange,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier= Modifier
                    .background(SurfaceOrange)
                    .padding(10.dp)

                ,
            ) {
                Text(title)
                Row() {
                    val maxChar = 17
                    TextField(
                        value = text,
                        onValueChange = {
                            if (it.length <= maxChar) text = it
                        },
                        label = { Text("Select Cuisine") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                data.value = text
                                stateValue.value = false
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
                        stateValue.value = false
                        data.value = ""
                        text = ""
                    }) {
                        Text(text = "Remove")
                    }
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingDialog(){
    AlertDialog(
        onDismissRequest = {

        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .wrapContentSize(Alignment.Center)
                //, RoundedCornerShape(15.dp))
                //.clip(RoundedCornerShape(15.dp))
                .border(
                    width = 1.dp,
                    color = PrimaryOrange,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier= Modifier
                    .background(SurfaceOrange)
                    .padding(10.dp)

                ,
            ) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Please Wait")
                }
                Row(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Indicator()
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Currently generating your custom recipe")
                }
            }
        }
    }
}
