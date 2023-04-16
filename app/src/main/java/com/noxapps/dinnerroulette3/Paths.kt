package com.noxapps.dinnerroulette3

sealed class Paths(val Path:String) {
    object NewInput:Paths("NewInput")
    object Settings:Paths("Settings")
    object Recipe:Paths("Recipe")

}