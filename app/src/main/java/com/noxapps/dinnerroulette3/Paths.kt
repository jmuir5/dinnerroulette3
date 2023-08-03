package com.noxapps.dinnerroulette3

sealed class Paths(val Path:String) {
    object Home:Paths("Home")
    object NewInput:Paths("NewInput")
    object NatLanInput:Paths("NatLanInput")
    object SpecificRecipeInput:Paths("SpecificRecipeInput")
    object Search:Paths("Search")
    object Settings:Paths("Settings")
    object Recipe:Paths("Recipe")

}