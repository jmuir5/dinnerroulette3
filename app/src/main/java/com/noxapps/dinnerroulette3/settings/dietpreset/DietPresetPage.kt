package com.noxapps.dinnerroulette3.settings.dietpreset

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.search.CusTitButton
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.input.MultiDialog
import com.noxapps.dinnerroulette3.input.SettingsObject
import com.noxapps.dinnerroulette3.input.SingleDialog
import com.noxapps.dinnerroulette3.input.StyledLazyRow
import com.noxapps.dinnerroulette3.savedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPresetPage(
    viewModel: DietPresetViewModel = DietPresetViewModel(),
    navController: NavHostController
) {
    val currentPreset = remember { mutableStateOf(viewModel.blankPreset.id) }
    val newPreset = remember { mutableStateOf(viewModel.blankPreset.id) }
    val newName = remember { mutableStateOf(viewModel.blankPreset.name) }
    val presetName = remember { mutableStateOf(viewModel.blankPreset.name) }
    /*remember{ derivedStateOf {
        if (currentPreset.value.toInt() ==0||newName.value!="New Preset")newName
        else viewModel.presetBox[currentPreset.value].name}
    }*/
    var meatContentIndex by remember { mutableStateOf(viewModel.blankPreset.meatContent) }
    val primaryMeatValues = remember {
        mutableStateOf(
            (0..viewModel.primaryMeatItems.size).map { mutableStateOf(true) }.toMutableList()
        )
    }
    val primaryCarbValues = remember {
        mutableStateOf(
            (0..viewModel.primaryCarbItems.size).map { mutableStateOf(true) }.toMutableList()
        )
    }
    val exclIngredients = remember { mutableStateListOf<String>() }
    val tags = remember { mutableStateListOf<String>() }

    val resetFlag = remember { mutableStateOf(false) }


    //ui controllers
    val presetSelectionOpen = remember { mutableStateOf(false) }
    val editNameOpen = remember { mutableStateOf(false) }
    var meatContentExpanded by remember { mutableStateOf(false) }
    val removeIngredientsOpen = remember { mutableStateOf(false) }
    val tagsOpen = remember { mutableStateOf(false) }
    val saveAsNameOpen = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loadedFlag by remember { mutableStateOf(false) }

    var retrievedData by remember {
        mutableStateOf(
            SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
        )
    }


    if (!loadedFlag) {
        Log.d("debug_SP", "loading saved preferences")
        val loadedData = runBlocking { context.dataStore.data.first() }
        loadedData[savedPreferences]?.let {
            retrievedData = try {
                Json.decodeFromString<SettingsObject>(it)
            } catch (exception: Exception) {
                SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
            }
            Log.d("debug_SP", "Loaded saved preferences, saved to view model")

            newPreset.value = retrievedData.dietPreset
            Log.d("debug_SP", retrievedData.toString())
        }
        loadedFlag = true
    }

    //reset to new preset
    if (resetFlag.value) {
        currentPreset.value = 0L
        newPreset.value = 0L
        newName.value = "New Preset"
        presetName.value = "New Preset"
        meatContentIndex = 0
        primaryMeatValues.value.clear()
        (0..viewModel.primaryMeatItems.size).forEach {
            primaryMeatValues.value.add(mutableStateOf(true))
        }
        primaryCarbValues.value.clear()
        (0..viewModel.primaryCarbItems.size).forEach {
            primaryCarbValues.value.add(mutableStateOf(true))
        }
        exclIngredients.clear()
        tags.clear()

        resetFlag.value = false
    }

    //update to new preset
    if (newPreset.value != currentPreset.value) {
        Log.d("new preset", viewModel.presetBox[newPreset.value].toString())

        presetName.value = viewModel.presetBox[newPreset.value].name
        newName.value = viewModel.presetBox[newPreset.value].name
        meatContentIndex = viewModel.presetBox[newPreset.value].meatContent

        viewModel.convertToBools(
            viewModel.presetBox[newPreset.value].enabledMeat,
            primaryMeatValues,
            viewModel.primaryMeatItems
        )
        viewModel.convertToBools(
            viewModel.presetBox[newPreset.value].enabledCarb,
            primaryCarbValues,
            viewModel.primaryCarbItems
        )


        exclIngredients.clear()
        viewModel.presetBox[newPreset.value].excludedIngredients.forEach {
            exclIngredients.add(it)
        }

        tags.clear()
        viewModel.presetBox[newPreset.value].descriptiveTags.forEach {
            tags.add(it)
        }

        currentPreset.value = newPreset.value
    }

    val meatListCon = mutableListOf<String>()
    val carbListCon = mutableListOf<String>()
    viewModel.convertToStrings(primaryMeatValues, meatListCon, viewModel.primaryMeatItems)
    viewModel.convertToStrings(primaryCarbValues, carbListCon, viewModel.primaryCarbItems)

    val constructed = DietPreset(
        id = currentPreset.value,
        name = newName.value,
        meatContent = meatContentIndex,
        enabledMeat = meatListCon,
        enabledCarb = carbListCon,
        excludedIngredients = exclIngredients.toMutableList(),
        descriptiveTags = tags.toMutableList()
    )

    Scaffold(
         topBar = {
             TopAppBar(
                 title = {
                     Text(text = newName.value,
                         maxLines = 1,
                         overflow = TextOverflow.Ellipsis
                     )
                 },
                 navigationIcon = {
                     IconButton(
                         colors = IconButtonDefaults.iconButtonColors(
                             containerColor = MaterialTheme.colorScheme.primary,
                             contentColor = MaterialTheme.colorScheme.onPrimary
                         ),
                         onClick = {
                             navController.popBackStack()
                         }
                     ) {
                         Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                     }
                 },
                 colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = MaterialTheme.colorScheme.primary,
                     titleContentColor = MaterialTheme.colorScheme.onPrimary
                 ),
             )
         }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
                .padding(it)
        ) {

            Column {
                Column(
                    modifier = Modifier
                        .padding(24.dp, 24.dp, 24.dp, 0.dp)
                ) {
                    Row(                                                                  //tags
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preset:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = {
                            presetSelectionOpen.value = true
                        }) {
                            Text(text = "Change")
                        }
                    }
                    Row(                                                                  //tags
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preset Name:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = {
                            editNameOpen.value = true
                        }) {
                            Text(text = "Edit")
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clickable(onClick = {
                                meatContentExpanded = true
                            })
                            .padding(0.dp, 8.dp)
                    ) {
                        Text(
                            text = "Want To Include Meat?",
                            style = MaterialTheme.typography.titleMedium

                        )
                        DropdownMenu(
                            expanded = meatContentExpanded,
                            onDismissRequest = { meatContentExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp, 4.dp),

                            ) {
                            viewModel.meatContentItems.forEachIndexed() { index, s ->
                                DropdownMenuItem(
                                    onClick = {
                                        meatContentIndex = index
                                        meatContentExpanded = false
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
                        Text(
                            text = viewModel.meatContentItems[meatContentIndex],
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                }
                if (meatContentIndex == 0) {
                    Text(
                        text = "Enabled Meat Items:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(24.dp, 24.dp, 24.dp, 0.dp)
                    )
                    TITLazyRow(
                        values = viewModel.primaryMeatItems,
                        states = primaryMeatValues.value,
                        icons = viewModel.primaryMeatIcons,
                        24.dp
                    )
                }
                Text(
                    text = "Enabled Carbohydrate Items:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(24.dp, 24.dp, 24.dp, 0.dp)
                )
                TITLazyRow(
                    values = viewModel.primaryCarbItems,
                    states = primaryCarbValues.value,
                    icons = viewModel.primaryCarbIcons,
                    24.dp
                )

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
                StyledLazyRow(array = exclIngredients, true, 24.dp)
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
                StyledLazyRow(array = tags, true, 24.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { navController.popBackStack() }) {
                        Text(text = "Discard Changes")
                    }

                    Button(
                        onClick = {
                            viewModel.presetBox.put(constructed)
                            retrievedData.dietPreset = currentPreset.value
                            scope.launch {
                                context.dataStore.edit { settings ->
                                    settings[savedPreferences] = Json.encodeToString(retrievedData)
                                }
                            }
                            navController.popBackStack()



                        },
                        enabled = if (currentPreset.value < 8L) false
                                else {
                                    constructed != viewModel.presetBox[currentPreset.value]
                                }

                    )
                    {
                        Text(text = "Save")
                    }


                    Button(
                        onClick = {
                            if(currentPreset.value ==0L){
                                if (constructed.name == viewModel.blankPreset.name){
                                    saveAsNameOpen.value = true
                                }
                            }
                            else {
                                if (constructed.name == viewModel.presetBox[currentPreset.value].name) {
                                    saveAsNameOpen.value = true
                                }
                            }
                            if (!saveAsNameOpen.value) {
                                constructed.id = 0
                                viewModel.presetBox.put(constructed)
                                retrievedData.dietPreset = constructed.id
                                scope.launch {
                                    context.dataStore.edit { settings ->
                                        settings[savedPreferences] =
                                            Json.encodeToString(retrievedData)
                                    }
                                }
                                navController.popBackStack()
                            }

                        },
                        enabled = if (currentPreset.value ==0L){
                                    constructed != viewModel.blankPreset
                                }
                                else {
                                    constructed != viewModel.presetBox[currentPreset.value]
                                }

                    ) {
                        Text(text = "Save as new")
                    }
                }

                //todo save button, save to objectbox, save id to shared preferences
            }
            if (presetSelectionOpen.value) {
                DietSelectDialog(presetSelectionOpen, viewModel.presetBox.all, "Select Preset", newPreset, resetFlag)
            }

            if (editNameOpen.value) {
                SingleDialog(editNameOpen, "Preset Name", "Name", newName, true)
            }

            if (removeIngredientsOpen.value) {
                MultiDialog(removeIngredientsOpen, "Exclude Ingredients", "Ingredient", exclIngredients)
            }

            if (tagsOpen.value) {
                MultiDialog(tagsOpen, "Descriptive Tags", "Tag", tags)
            }
            if (saveAsNameOpen.value){
                SaveAsNameDialog(
                    stateValue = saveAsNameOpen,
                    title = "Save As...",
                    label = "",
                    data = constructed,
                    settingsObject = retrievedData,
                    navController = navController
                )
            }


        }
        /*
        excluded protien sources
        excluded carbohydrates
         */
    }
}



@Composable
fun TITLazyRow(
    values: List<String>,
    states:MutableList<MutableState<Boolean>>,
    icons:Pair<ImageVector, ImageVector>,
    falsePadding: Dp = 0.dp
){
    if (values.size!= states.size) {
        states.clear()
        values.forEach{ _ ->
            states.add(mutableStateOf(true))
        }
    }

    LazyRow(modifier = Modifier
        .fillMaxWidth()
    ) {
        if(values.isNotEmpty()) {
            item() {
                Spacer(modifier = Modifier.size(falsePadding))
            }
            values.forEachIndexed() { index, value ->
                item() {
                    CusTitButton(
                        text =value,
                        value = states[index].value,
                        onClick = {
                            states[index].value = !states[index].value
                        },
                        iconInit = icons.first,//Icons.Filled.FavoriteBorder,
                        iconChecked = icons.second
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
            item() {
                Spacer(modifier = Modifier.size(falsePadding))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietSelectDialog(
    stateValue: MutableState<Boolean>,
    options:List<DietPreset>,
    title: String,
    selected: MutableState<Long>,
    resetFlag:MutableState<Boolean>
) {
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
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),){
                        item(){
                            Button(modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    resetFlag.value=true
                                    stateValue.value = false
                                }) {
                                Row (modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "New Preset"
                                    )

                                    Text("New Preset")
                                    Spacer(modifier = Modifier.size(1.dp))
                                }
                            }
                        }
                        options.forEach {
                            item(){
                                Button(modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        selected.value = it.id
                                        stateValue.value=false
                                    }) {
                                    Row (modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween){
                                        Icon(
                                            imageVector = Icons.Filled.Article,
                                            contentDescription = it.name
                                        )
                                        Text(it.name)
                                        Spacer(modifier = Modifier.size(1.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.size(2.dp))
                            }
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
                        stateValue.value = false
                    }) {
                        Text(text = "Cancel")
                    }
                }

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveAsNameDialog(
    stateValue: MutableState<Boolean>,
    title: String,
    label: String,
    data: DietPreset,
    settingsObject: SettingsObject,
    navController: NavHostController
) {
    val focusRequester = remember { FocusRequester() }
    var text = remember { mutableStateOf(TextFieldValue(data.name)) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
                            .onFocusChanged { focusState ->
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
                                if (text.value.text.isNotEmpty()&& text.value.text!=data.name) {
                                    data.name = text.value.text
                                    stateValue.value = false
                                    data.id = 0
                                    ObjectBox.store.boxFor(DietPreset::class.java).put(data)
                                    settingsObject.dietPreset = data.id
                                    scope.launch {
                                        context.dataStore.edit { settings ->
                                            settings[savedPreferences] =
                                                Json.encodeToString(settingsObject)
                                        }
                                    }
                                    navController.popBackStack()
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
                    }) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            data.name = text.value.text
                            stateValue.value = false
                            data.id = 0
                            ObjectBox.store.boxFor(DietPreset::class.java).put(data)
                            settingsObject.dietPreset = data.id
                            scope.launch {
                                context.dataStore.edit { settings ->
                                    settings[savedPreferences] =
                                        Json.encodeToString(settingsObject)
                                }
                            }
                            navController.popBackStack()
                        },
                        enabled = (text.value.text.isNotEmpty()&& text.value.text!=data.name)
                        ) {
                        Text(text = "Save As")
                    }
                }

            }

        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
