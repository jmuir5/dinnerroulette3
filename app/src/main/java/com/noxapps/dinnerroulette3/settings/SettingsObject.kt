package com.noxapps.dinnerroulette3.settings

import kotlinx.serialization.Serializable

@Serializable
class SettingsObject(
    val imperial: Boolean,
    val fahrenheit: Boolean,
    val skill:Int,
    var dietPreset:Long,
    val budget:Int,
    )