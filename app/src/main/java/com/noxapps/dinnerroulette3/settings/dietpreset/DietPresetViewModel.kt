package com.noxapps.dinnerroulette3.settings.dietpreset

import androidx.lifecycle.ViewModel
import com.noxapps.dinnerroulette3.ObjectBox
import io.objectbox.Box

class DietPresetViewModel: ViewModel() {
    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)
}