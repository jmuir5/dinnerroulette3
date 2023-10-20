package com.noxapps.dinnerroulette3.settings.dietpreset

import androidx.lifecycle.ViewModel
import com.noxapps.dinnerroulette3.ObjectBox
import io.objectbox.Box

class DietPresetViewModel: ViewModel() {
    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)

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
    val primaryCarbItems = listOf("Pasta", "Potato", "Rice", "Noodles", "Bread", "Other")

}