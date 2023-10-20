package com.noxapps.dinnerroulette3

/**
 * paths for nav controller
 */
sealed class Paths(val Path:String) {
    object Home:Paths("Home")
    object NewInput:Paths("NewInput")
    object NatLanInput:Paths("NatLanInput")
    object SpecificRecipeInput:Paths("SpecificRecipeInput")
    object Search:Paths("Search")
    object Settings:Paths("Settings")
    object DietPreset:Paths("DietPreset")
    object Recipe:Paths("Recipe")
    object Error:Paths("Error")
    object Redeem:Paths("Redeem")

}