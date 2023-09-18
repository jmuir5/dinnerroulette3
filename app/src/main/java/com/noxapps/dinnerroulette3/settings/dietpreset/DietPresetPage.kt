package com.noxapps.dinnerroulette3.settings.dietpreset

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.NoFood
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.search.CusTitButton
import com.noxapps.dinnerroulette3.DrawerAndScaffold
import com.noxapps.dinnerroulette3.input.MultiDialog
import com.noxapps.dinnerroulette3.input.SingleDialog
import com.noxapps.dinnerroulette3.input.StyledLazyRow

@Composable
fun DietPresetPage(
    viewModel: DietPresetViewModel = DietPresetViewModel(),
    navController: NavHostController
) {
    //val presetBox = ObjectBox.store.boxFor(DietPreset::class.java)
    val currentPreset = remember{mutableStateOf(0L)}
    val newName = remember{mutableStateOf("New Preset")}//todo add new preset name holder
    val presetName = remember{ derivedStateOf {
        if (currentPreset.value.toInt() ==0||newName.value!="New Preset")newName
        else viewModel.presetBox[currentPreset.value].name}
    }



    var meatContentIndex by remember { mutableStateOf(0) }
    val meatContentItems = listOf("Yes", "No - Vegetarian", "No - Vegan")

    val primaryMeatItems = listOf(
        "Beef",
        "Chicken",
        "Pork",
        "Lamb",
        "Shellfish",
        "Salmon",
        "White Fish"
    )
    val primaryMeatValues = mutableListOf<MutableState<Boolean>>()
    val primaryMeatIcons = Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)//todo custom icons?

    val primaryCarbItems = listOf("Pasta", "Potato", "Rice", "Noodles", "Bread", "Other")
    val primaryCarbValues = mutableListOf<MutableState<Boolean>>()
    val primaryCarbIcons = Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)//todo custom icons?

    val exclIngredients = remember { mutableStateListOf<String>() }

    val tags = remember { mutableStateListOf<String>() }


    //ui controllers
    val presetSelectionOpen = remember { mutableStateOf(false) }
    val editNameOpen = remember { mutableStateOf(false) }
    var meatContentExpanded by remember { mutableStateOf(false) }
    val removeIngredientsOpen = remember { mutableStateOf(false) }
    val tagsOpen = remember { mutableStateOf(false) }



    DrawerAndScaffold(tabt = "Preset: "+presetName.value, navController = navController) {
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
                    //todo select dietary preset
                    //todo edit preset name
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
                            meatContentItems.forEachIndexed() { index, s ->
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
                            text = meatContentItems[meatContentIndex],
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
                        values = primaryMeatItems,
                        states = primaryMeatValues,
                        icons = primaryMeatIcons,
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
                    values = primaryCarbItems,
                    states = primaryCarbValues,
                    icons = primaryCarbIcons,
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
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Save Diet Preset")
                }

                //todo save button, save to objectbox, save id to shared preferences
            }
            if (presetSelectionOpen.value) {
                DietSelectDialog(presetSelectionOpen, viewModel.presetBox.all, "Select Preset",currentPreset )
            }

            if (editNameOpen.value) {
                SingleDialog(editNameOpen, "Descriptive Tags", newName)
            }

            if (removeIngredientsOpen.value) {
                MultiDialog(removeIngredientsOpen, "Exclude Ingredients", exclIngredients)
            }

            if (tagsOpen.value) {
                MultiDialog(tagsOpen, "Descriptive Tags", tags)
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
        states.forEach {
            states.remove(it)
        }
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
    selected: MutableState<Long>
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
                                    selected.value = 0
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
