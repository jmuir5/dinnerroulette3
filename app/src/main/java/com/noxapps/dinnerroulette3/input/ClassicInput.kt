package com.noxapps.dinnerroulette3.input

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.commons.Indicator
import com.noxapps.dinnerroulette3.InterstitialAdDialogue
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.UserStore
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.loadInterstitialAd
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.ui.theme.md_theme_light_secondaryContainer
import com.noxapps.dinnerroulette3.usedTokens
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * ui code for classic input
 * needs to be rewritten to fit with new ui design paradigms
 * maybe need to remove the drawer?
 * it might be a good idea to take the stopper and disclamer dialogues and extract most of the code
 * into a "text dialogue" composable functionbut then ill be passing a collosal string to the function
 * instead of declaring it locally. probably better tbh.
 *
 * i would like to remove the dialogue composables and move them to a "commons" file but im
 * unsure of the potential reprecussions. perhaps thats a job for next friday
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewInput(
    viewModel: InputViewModel = InputViewModel(),
    navController: NavHostController
) {

    StandardScaffold(tabt = "Create Custom Recipe", navController = navController) {
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
            listOf("Select...", "Any", "Pasta", "Potato", "Rice", "Noodles", "Bread", "Other", "None")

        val budgetItems = listOf("Select...","$","$$","$$$")


        var text by remember { mutableStateOf("") }

        val cuisineText = remember { mutableStateOf("") }

        val ingredients = remember { mutableStateListOf<String>() }
        val exclIngredients = remember { mutableStateListOf<String>() }
        val tags = remember { mutableStateListOf<String>() }


        var meatContentIndex by remember { mutableStateOf(0) }
        var primaryMeatIndex by remember { mutableStateOf(0) }
        var primaryCarbIndex by remember { mutableStateOf(0) }
        var budgetIndex by remember {mutableStateOf(0)}


        val cuisine = remember { mutableStateOf(false) }
        val addIngredientsOpen = remember { mutableStateOf(false) }
        val removeIngredientsOpen = remember { mutableStateOf(false) }
        val tagsOpen = remember { mutableStateOf(false) }
        val budgetOpen = remember { mutableStateOf(false) }

        val processing = remember { mutableStateOf(false) }

        val context = LocalContext.current
        val store = UserStore(context)

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.roulette_table)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap()) }
        val buttonSize = (LocalConfiguration.current.screenWidthDp/2.5).dp

        var stopper by remember { mutableStateOf(false) }
        val stopperFlag by remember { mutableStateOf(false) }
        var disclamer by remember { mutableStateOf(false) }

        var loadedFlag by remember { mutableStateOf(false) }

        if (!loadedFlag) {
            val loadedData = runBlocking { context.dataStore.data.first() }
            loadedData[usedTokens]?.let { Log.d("tokens used", it.toString()) }
            loadedData[usedTokens]?.let {
                if (it > 5000) {
                    //stopper = true
                    //stopperFlag = true
                }
            }

            loadedData[savedPreferences]?.let {
                val retrievedData: SettingsObject = try {
                    Json.decodeFromString<SettingsObject>(it)
                }catch(exception: Exception){
                    SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
                }
                meatContentIndex = retrievedData.meatContent
                budgetIndex = retrievedData.budget

            }
        }
        loadedFlag = true

        val adFrameFlag = remember { mutableStateOf(false) }
        val adReference = if(BuildConfig.DEBUG){
            LocalContext.current.getString(R.string.test_roulette_interstitial_ad_id)
        }
        else LocalContext.current.getString(R.string.roulette_interstitial_ad_id)
        var loadAttempted by remember{mutableStateOf(false)}
        if(!loadAttempted) {
            loadInterstitialAd(context, viewModel.mInterstitialAd, viewModel.TAG2, adReference)
            loadAttempted=true
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
        ) {
            Column() {
                Column(modifier = Modifier
                    .padding(24.dp, 24.dp, 24.dp, 0.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clickable(onClick = {
                                dd1Expanded = true
                            })
                            .padding(0.dp, 8.dp)
                    ) {
                        Text(
                            text = "Want To Include Meat?",
                            style = MaterialTheme.typography.titleMedium

                        )
                        DropdownMenu(
                            expanded = dd1Expanded,
                            onDismissRequest = { dd1Expanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp, 4.dp),

                            ) {
                            meatContentItems.forEachIndexed() { index, s ->
                                if (index != 0) {
                                    DropdownMenuItem(
                                        onClick = {
                                            meatExpanded = index == 1 || index == 2
                                            if (!carbExpanded) carbExpanded = index == 3 || index == 4
                                            meatContentIndex = index

                                            dd1Expanded = false
                                        }, text = {
                                            Text(
                                                text = s,
                                                textAlign = TextAlign.End,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(0.dp, 4.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                        Text(
                            text = meatContentItems[meatContentIndex],
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    if (meatExpanded) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .clickable(onClick = {
                                    dd2Expanded = true
                                })
                                .padding(0.dp, 8.dp)
                        ) {
                            Text(
                                text = "What Primary Meat?",
                                style = MaterialTheme.typography.titleMedium

                            )
                            DropdownMenu(
                                expanded = dd2Expanded,
                                onDismissRequest = { dd2Expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp, 4.dp),
                            ) {
                                primaryMeatItems.forEachIndexed() { index, s ->
                                    if (index != 0) {
                                        DropdownMenuItem(
                                            modifier = Modifier
                                                .padding(0.dp, 4.dp)
                                                .fillMaxWidth(),
                                            onClick = {
                                                carbExpanded = true
                                                primaryMeatIndex = index
                                                dd2Expanded = false

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
                                text = primaryMeatItems[primaryMeatIndex],
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                    }
                    if (carbExpanded) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .clickable(onClick = {
                                    dd3Expanded = true
                                })
                                .padding(0.dp, 8.dp)
                        ) {
                            Text(
                                text = "What Primary Carbohydrate?",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                            )
                            DropdownMenu(
                                expanded = dd3Expanded,
                                onDismissRequest = { dd3Expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp, 4.dp),
                            ) {
                                primaryCarbItems.forEachIndexed() { index, s ->
                                    if (index != 0) {
                                        DropdownMenuItem(
                                            modifier = Modifier
                                                .padding(0.dp, 4.dp)
                                                .fillMaxWidth(),
                                            onClick = {
                                                additionalExpanded = true
                                                primaryCarbIndex = index
                                                dd3Expanded = false
                                            }, text = {
                                                Text(
                                                    text = s,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                )
                                            })
                                    }
                                }
                            }
                            Text(
                                text = primaryCarbItems[primaryCarbIndex],
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                    }
                }
                if (additionalExpanded) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp, 0.dp, 24.dp, 0.dp)
                    ) {
                        Row(                                                               //cuisine
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp)
                        ) {
                            Text(
                                text = "Cuisine:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = {
                                cuisine.value = true
                            }) {
                                Text(
                                    text =
                                    if (cuisineText.value.isEmpty()) "Edit"
                                    else cuisineText.value
                                )
                            }

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
                        Row(                                                       //add ingredients
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Add Ingredients:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = {
                                addIngredientsOpen.value = true
                            }) {
                                Text(text = "Edit")
                            }
                        }
                    }
                    StyledLazyRow(array = ingredients, false, 24.dp)
                    Column( //excl ingredients
                        modifier = Modifier
                            .padding(24.dp, 0.dp, 24.dp, 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Excluded Ingredients:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = {
                                removeIngredientsOpen.value = true
                            }) {
                                Text(text = "Edit")
                            }

                        }
                    }
                    StyledLazyRow(array = exclIngredients, false, 24.dp)
                    Column(
                        modifier = Modifier
                            .padding(24.dp, 0.dp, 24.dp, 0.dp)
                    ) {
                        Row(                                                                  //tags
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Descriptive Tags:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = {
                                tagsOpen.value = true
                            }) {
                                Text(text = "Edit")
                            }
                        }
                    }
                    StyledLazyRow(array = tags, false, 24.dp)
                    Column(
                        modifier = Modifier
                            .padding(24.dp, 0.dp, 24.dp, 0.dp)
                    ) {
                        Box(                                                                //button
                            Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "ChefRoulette",
                                modifier = Modifier
                                    //.fillMaxWidth()
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
                                    if(viewModel.recipeBox.all.size<2){
                                        val query = Query(
                                            meatContentItems[meatContentIndex],
                                            primaryMeatItems[primaryMeatIndex],
                                            primaryCarbItems[primaryCarbIndex],
                                            cuisineText.value,
                                            ingredients,
                                            exclIngredients,
                                            tags
                                        )
                                        viewModel.executeClassic(
                                            query,
                                            processing,
                                            context,
                                            navController
                                        )
                                    }
                                    else {
                                        adFrameFlag.value = true
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Gray
                                )
                            ) {
                                Text(
                                    text = "Generate Recipe",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        if (stopperFlag) {
                            Text(text = "All Beta Tokens Used")
                        }

                    }
                }

            }
        }

        if (cuisine.value) {
            SingleDialog(cuisine, "Cuisine", "Select Cuisine", cuisineText)
        }

        if (addIngredientsOpen.value) {
            MultiDialog(addIngredientsOpen, "Include Ingredients", "Ingredient", ingredients)
        }

        if (removeIngredientsOpen.value) {
            MultiDialog(removeIngredientsOpen, "Exclude Ingredients", "Ingredient",exclIngredients)
        }

        if (tagsOpen.value) {
            MultiDialog(tagsOpen, "Descriptive Tags", "Tag", tags)
        }

        if (processing.value) {
            ProcessingDialog()
        }

        if(adFrameFlag.value){
            InterstitialAdDialogue(
                mInterstitialAd = viewModel.mInterstitialAd,
                context = context,
                displayFlag = adFrameFlag,
                function = {
                    val query = Query(
                        meatContentItems[meatContentIndex],
                        primaryMeatItems[primaryMeatIndex],
                        primaryCarbItems[primaryCarbIndex],
                        cuisineText.value,
                        ingredients,
                        exclIngredients,
                        tags,
                        budgetIndex
                    )
                    viewModel.executeClassic(
                        query,
                        processing,
                        context,
                        navController
                    )
                }
            )
        }

        if (stopper) {
            AlertDialog(
                onDismissRequest = {
                    stopper = false
                }
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(15.dp)
                        ),

                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(10.dp),
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
                        .wrapContentSize(Alignment.Center)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(15.dp)
                        ),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.background)
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
}

/**
 * customised alert dialogue feturing a title, a text box and a cancel button, and a list of current
 * array entries. Designed for declaring multiple things to an [array]
 * [stateValue] : mutable boolean declaring whether the dialogue should be shown
 * [title] : the title for the dialogue
 * [array] : the target array to add/ remove elements from
 *
 * todo:
 * fix code to display excess list elements on new lines
 * give list elements a rounded shape
 * smooth out element placements
 * make background white / change based on current ui paradigm
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    label: String,
    array: SnapshotStateList<String>
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            stateValue.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                ,
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier= Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title,
                    style = MaterialTheme.typography.titleLarge
                )
                StyledLazyRow(array = array, true)
                Row() {
                    val maxChar = 17
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester),
                        value = text,
                        onValueChange = {
                            if (it.length <= maxChar) text = it
                        },
                        label = { Text(label) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if(text.isNotEmpty()) {
                                    array.add(text)
                                    text = ""
                                }
                            }
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = {
                        stateValue.value = false
                        text = ""
                    }) {
                        Text(text = "Return")
                    }
                    Button(onClick = {
                        if(text.isNotEmpty()) {
                            array.add(text)
                            text = ""
                        }
                    }) {
                        Text(text = "Add")
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

/**
 * customised alert dialogue feturing a title, a text box and a cancel button. designed for
 * setting a string value
 * [stateValue] : mutable boolean declaring whether the dialogue should be shown
 * [title] : the title for the dialogue
 * [data] : the target string to change
 *
 * todo:
 * title text style
 * smooth out element placements
 * make background white / change based on current ui paradigm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    label: String,
    data: MutableState<String>,
    persistenceFlag:Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    var text = remember { mutableStateOf(TextFieldValue("")) }
    if(persistenceFlag)text.value = TextFieldValue(data.value)
    AlertDialog(
        onDismissRequest = {
            stateValue.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                ,
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation

        ) {
            Column(
                modifier= Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title,
                    style = MaterialTheme.typography.titleLarge)
                Row() {
                    val maxChar = 17
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged {  focusState ->
                                if (focusState.isFocused) {
                                    val text2 = text.value.text
                                    text.value = text.value.copy(
                                        selection = TextRange(0, text2.length)
                                    )
                                }
                            },
                        value = text.value,
                        onValueChange = {
                            if (it.text.length <= maxChar) text.value = it
                        },
                        //label = { Text(label) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (text.value.text.isNotEmpty()) {
                                    data.value = text.value.text
                                    stateValue.value = false
                                    text.value = TextFieldValue("")
                                }

                            }
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        stateValue.value = false
                        data.value = ""
                        text.value = TextFieldValue("")
                    }) {
                        Text(text = "Clear")
                    }
                    Button(onClick = {
                        if (text.value.text.isNotEmpty()) {
                            data.value = text.value.text
                            stateValue.value = false
                            text.value = TextFieldValue("")
                        }
                    }) {
                        Text(text = "Save")
                    }
                }

            }

        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}


/**
 * customised alert dialogue featuring text to inform the user that the app is processing something
 * and a visual indicator to shot that the process is still running. intentionally designed to be
 * uninterruptable.
 *
 * todo:
 * text style
 * make background white / change based on current ui paradigm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingDialog(){
    AlertDialog(
        onDismissRequest = {

        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier= Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Please Wait",
                        style = MaterialTheme.typography.titleLarge)
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

@Composable
fun StyledLazyRow(array: SnapshotStateList<String>, staticHeight:Boolean = false, falsePadding:Dp = 0.dp){
    LazyRow(modifier = Modifier
        .fillMaxWidth()
    ) {
        if(array.isNotEmpty()) {
            item() {
                Spacer(modifier = Modifier.size(falsePadding))
            }
            array.forEach() { s ->
                item() {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(md_theme_light_secondaryContainer)
                        .padding(3.dp)
                        .clickable {
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
                    Spacer(modifier = Modifier.size(2.dp))
                }
            }
            item() {
                Spacer(modifier = Modifier.size(falsePadding))
            }
        }
        if(staticHeight){
            if (array.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                    ) {
                        Row() {
                            Text(" ")
                        }
                    }
                }
            }
        }

    }
}
