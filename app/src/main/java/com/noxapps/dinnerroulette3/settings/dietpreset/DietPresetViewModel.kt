package com.noxapps.dinnerroulette3.settings.dietpreset

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.settings.SettingsObject
import io.objectbox.Box

class DietPresetViewModel: ViewModel() {


    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)

    val meatContentItems = listOf("Yes", "No - Vegetarian", "No - Vegan")
    val primaryMeatItems = listOf(
        "Beef",
        "Chicken",
        "Pork",
        "Lamb",
        "Seafood",
        "Salmon",
        "White Fish",
        "Shellfish",
        "Egg",
        "Legumes"
    )
    val primaryCarbItems = listOf("Pasta", "Potato", "Rice", "Noodles", "Bread", "Other")

    val primaryMeatIcons = Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)//todo custom icons?

    val primaryCarbIcons = Pair(Icons.Outlined.NoFood, Icons.Filled.Fastfood)//todo custom icons?



    lateinit var retrievedData: SettingsObject


    val blankPreset = DietPreset(0,
        "New Preset",
        0,
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf())

    fun convertToStrings(
        data: MutableState<MutableList<MutableState<Boolean>>>,
        output:MutableList<String>,
        names:List<String>){
        names.forEachIndexed { index, it ->
            if (!data.value[index].value) output.add(it)
        }
    }
    fun convertToBools(
        data: MutableList<String>,
        output:MutableState<MutableList<MutableState<Boolean>>>,
        names:List<String>){
        output.value.clear()
        names.forEach {
            if (data.contains(it)) {
                output.value.add(mutableStateOf(false))
            } else {
                output.value.add(mutableStateOf(true))
            }
        }
    }



}