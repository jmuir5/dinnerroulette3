package com.noxapps.dinnerroulette3.input

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.InterstitialAdDialogue
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.commons.MultiDialog
import com.noxapps.dinnerroulette3.commons.PrimaryItemSelector
import com.noxapps.dinnerroulette3.commons.ProcessingDialog
import com.noxapps.dinnerroulette3.commons.SingleDialog
import com.noxapps.dinnerroulette3.commons.StyledLazyRow
import com.noxapps.dinnerroulette3.commons.TwinCounterDialog
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.loadInterstitialAd
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.settings.SettingsObject
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
 * unsure of the potential reprecussions.
 */

@Composable
fun NewInput(
    viewModel: InputViewModel = InputViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    val adflag = getAdFlag(context)

    StandardScaffold(tabt = "Create Custom Recipe", navController = navController, adFlag = adflag) {
        var dd1Expanded by remember { mutableStateOf(false) }
        var dd2Expanded by remember { mutableStateOf(false) }
        var dd3Expanded by remember { mutableStateOf(false) }

        val enabledMeat = remember { mutableStateListOf<String>() }
        val enabledCarb = remember { mutableStateListOf<String>() }

        val cuisineText = remember { mutableStateOf("") }
        val adultServings = remember{ mutableIntStateOf(0) }
        val childServings = remember{ mutableIntStateOf(0) }
        val ingredients = remember { mutableStateListOf<String>() }
        val exclIngredients = remember { mutableStateListOf<String>() }
        val tags = remember { mutableStateListOf<String>() }


        var meatContentIndex by remember { mutableStateOf(0) }
        var primaryMeatIndex = remember { mutableIntStateOf(0) }
        var primaryCarbIndex = remember { mutableIntStateOf(0) }
        var budgetIndex by remember {mutableStateOf(0)}

        val meatExpanded = remember { derivedStateOf { meatContentIndex==1 } }
        val carbExpanded = remember { derivedStateOf { meatContentIndex > 0 }}
        val additionalExpanded = remember { derivedStateOf { primaryCarbIndex.intValue>0 }}

        val meatText = remember{ mutableStateOf("") }
        val carbText = remember{ mutableStateOf("") }

        val cuisineOpen = remember { mutableStateOf(false) }
        val servingSizeOpen = remember { mutableStateOf(false) }
        val addIngredientsOpen = remember { mutableStateOf(false) }
        val removeIngredientsOpen = remember { mutableStateOf(false) }
        val tagsOpen = remember { mutableStateOf(false) }
        val budgetOpen = remember { mutableStateOf(false) }

        val processing = remember { mutableStateOf(false) }

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.roulette_table)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap()) }
        val buttonSize = (LocalConfiguration.current.screenWidthDp/2.5).dp

        var loadedFlag by remember { mutableStateOf(false) }

        if (!loadedFlag) {
            viewModel.primaryMeatItems.forEach{
                enabledMeat.add(it)
            }
            viewModel.primaryCarbItems.forEach{
                enabledCarb.add(it)
            }
            val loadedData = runBlocking { context.dataStore.data.first() }
            loadedData[savedPreferences]?.let {
                val retrievedData: SettingsObject = try {
                    Json.decodeFromString(it)
                }catch(exception: Exception){
                    SettingsObject(false, false, 0, 0, 0)
                }

                budgetIndex = retrievedData.budget
                if (retrievedData.dietPreset>1) {
                    val thisPreset = viewModel.presetBox[retrievedData.dietPreset]
                    meatContentIndex = thisPreset.meatContent + 1
                    if(thisPreset.meatContent>0) meatContentIndex+=1
                    thisPreset.enabledMeat.forEach { meat ->
                        enabledMeat.remove(meat)
                    }
                    thisPreset.enabledCarb.forEach { carb ->
                        enabledCarb.remove(carb)
                    }
                    thisPreset.excludedIngredients.forEach { excl ->
                        exclIngredients.add(excl)
                    }
                    thisPreset.descriptiveTags.forEach { tag ->
                        tags.add(tag)
                    }
                }

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
                            viewModel.meatContentItems.forEachIndexed() { index, s ->
                                if (index != 0) {
                                    DropdownMenuItem(
                                        onClick = {
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
                            text = viewModel.meatContentItems[meatContentIndex],
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    if (meatExpanded.value) {
                        PrimaryItemSelector(
                            "Meat",
                            primaryMeatIndex,
                            enabledMeat,
                            meatText,
                            Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)
                        )
                    }
                    if (carbExpanded.value) {
                        PrimaryItemSelector(
                            "Carbohydrate",
                            primaryCarbIndex,
                            enabledCarb,
                            carbText,
                            Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)
                        )
                    }
                }
                if (additionalExpanded.value) {
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
                                cuisineOpen.value = true
                            }) {
                                Text(
                                    text =
                                    if (cuisineText.value.isEmpty()) "Edit"
                                    else cuisineText.value
                                )
                            }

                        }
                        Row(                                                              //servings
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp)
                        ) {
                            Text(
                                text = "Servings:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = {
                                servingSizeOpen.value = true
                            }) {
                                Text(
                                    text = if(adultServings.intValue+childServings.intValue ==0)"Default"
                                    else adultServings.intValue.toString()+"/"+childServings.intValue.toString()
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
                                viewModel.budgetItems.forEachIndexed() { index, s ->
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
                                text = viewModel.budgetItems[budgetIndex],
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
                                onClick = {
                                    if(viewModel.recipeBox.all.size<2 || !adflag){
                                        val query = Query(
                                            viewModel.meatContentItems[meatContentIndex],
                                            meatText.value,
                                            carbText.value,
                                            cuisineText.value,
                                            Pair(adultServings.intValue, childServings.intValue),
                                            ingredients,
                                            exclIngredients,
                                            tags
                                        )
                                        try {
                                            viewModel.executeClassic(
                                                query,
                                                processing,
                                                context,
                                                navController
                                            )
                                        }
                                        catch (e:Exception){
                                            navController.navigate(Paths.Error.Path+"/${e}")
                                        }

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
                    }
                }

            }
        }

        if (cuisineOpen.value) {
            SingleDialog(cuisineOpen, "Cuisine", "Select Cuisine", cuisineText)
        }

        if (servingSizeOpen.value) {
            TwinCounterDialog(
                stateValue = servingSizeOpen,
                title = "Serves",
                label1 = "Adults:",
                data1 = adultServings,
                label2 = "Children:",
                data2 = childServings
            )
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
            ProcessingDialog("Currently generating your custom recipe")
        }

        if(adFrameFlag.value){
            InterstitialAdDialogue(
                mInterstitialAd = viewModel.mInterstitialAd,
                context = context,
                displayFlag = adFrameFlag,
                function = {
                    val query = Query(
                        viewModel.meatContentItems[meatContentIndex],
                        meatText.value,
                        carbText.value,
                        cuisineText.value,
                        Pair(adultServings.intValue, childServings.intValue),
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
    }
}