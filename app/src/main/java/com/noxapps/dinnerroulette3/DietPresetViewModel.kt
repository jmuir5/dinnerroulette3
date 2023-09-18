package com.noxapps.dinnerroulette3

import androidx.lifecycle.ViewModel
import io.objectbox.Box

class DietPresetViewModel: ViewModel() {
    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)
}