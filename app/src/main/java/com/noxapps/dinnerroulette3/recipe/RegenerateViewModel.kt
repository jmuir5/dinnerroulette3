package com.noxapps.dinnerroulette3.recipe

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.gpt.getResponse
import com.noxapps.dinnerroulette3.gpt.parseResponse
import com.noxapps.dinnerroulette3.input.ParsedResponse
import com.noxapps.dinnerroulette3.input.QandA
import com.noxapps.dinnerroulette3.input.Query
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.settings.SettingsObject
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
import com.noxapps.dinnerroulette3.usedTokens
import io.objectbox.Box
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RegenerateViewModel: ViewModel() {
    val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)
    val recipeBox: Box<SavedRecipe> = ObjectBox.store.boxFor(SavedRecipe::class.java)

    val similarityItems = listOf("Name Only", "Query", "Full Recipe")

    val TAG = "Regenerate Recipe Interstitial"

    val mInterstitialAd:MutableState<InterstitialAd?> = mutableStateOf(null)

    fun getPreset(context:Context):Long{
        var id = 0L
        val loadedData = runBlocking { context.dataStore.data.first() }
        loadedData[savedPreferences]?.let {
            val retrievedData: SettingsObject = try {
                Json.decodeFromString(it)
            }catch(exception: Exception){
                SettingsObject(false, false, 0, 0, 0)
            }

            id = retrievedData.dietPreset

        }
        return id
    }
    fun regenerateName(
        promptText:String,
        modifiers: SnapshotStateList<String>,
        processingDialogueFlag: MutableState<Boolean>,
        saveID:Long,
        presetId:Long,
        context: Context,
        navController: NavHostController
    ){
        Log.d("debug", "regenerating - name started")
        var request = if (presetId <2){
            "Give me a recipe for $promptText."
        }
        else{
            presetRequest(promptText, presetId, modifiers)
        }
        if (presetId<2){
            if (modifiers.isNotEmpty()) {
                request += "apply the following modifiers to the recipe: "
                modifiers.forEach { s -> request += "$s, " }
            }
            request+="[fin]"
        }
        Log.d("constructed question", request)
        processingDialogueFlag.value = true
        try {
            getResponse(
                request,
                context,
                1,
                errorCallback = {
                    MainScope().launch {
                        navController.navigate(Paths.Error.Path+"/"+it)
                    }
                }
            ) {
                val received: SavedRecipe
                try {
                    received = SavedRecipe(QandA(Query(), it, Json{ignoreUnknownKeys = true}.decodeFromString<ParsedResponse>(it.choices[0].message.content)))
                    received.id = saveID
                    recipeBox.put(received)

                    MainScope().launch {
                        val backStack = navController.currentBackStack.first()
                        val lastPage = backStack[backStack.size-1]
                        navController.navigate(Paths.Recipe.Path + "/" + received.id){
                            popUpTo(lastPage.destination.route?:"Search"){inclusive = true}
                        }
                    }

                } catch (e:Exception){
                    MainScope().launch {
                        navController.navigate(Paths.Error.Path+"/"+it.choices[0].message.content)
                    }
                }
            }
        } catch (e:Exception){
            MainScope().launch {
                navController.navigate(Paths.Error.Path+"/"+e)
            }
        }
    }
    fun regenerateQuery(
        query: Query,
        modifiers: SnapshotStateList<String>,
        processingStateFlag: MutableState<Boolean>,
        saveID: Long,
        context:Context,
        navController:NavHostController
    ){
        val question = generateQuestion(query,modifiers)
        Log.d("constructed question", question)
        processingStateFlag.value = true

        try {
            getResponse(
                question,
                context,
                0,
                errorCallback = {
                    MainScope().launch {
                        navController.navigate(Paths.Error.Path+"/"+it)
                    }
                }
            ) { it ->
                var received = SavedRecipe()
                try {
                    received = SavedRecipe(QandA(query, it, Json{ignoreUnknownKeys = true}.decodeFromString<ParsedResponse>(it.choices[0].message.content)))
                    Log.e("id before", received.id.toString())
                    received.id = saveID
                    recipeBox.put(received)

                    MainScope().launch {
                        val backStack = navController.currentBackStack.first()
                        val lastPage = backStack[backStack.size-1]
                        navController.navigate(Paths.Recipe.Path + "/" + received.id){
                            popUpTo(lastPage.destination.route?:"Search"){inclusive = true}
                        }
                    }
                } catch (e: Exception) {
                    MainScope().launch {
                        navController.navigate(Paths.Error.Path + "/" + it.choices[0].message.content)
                    }
                }
            }
        }catch (e: Exception) {
            MainScope().launch {
                navController.navigate(Paths.Error.Path + "/" + e)
            }
        }
    }

    fun regenerateFull(
        recipe: SavedRecipe,
        modifiers: SnapshotStateList<String>,
        processingStateFlag: MutableState<Boolean>,
        saveID: Long,
        context:Context,
        navController:NavHostController
    ){
        val question = recipeQuestion(recipe, modifiers)
        Log.d("constructed question", question)
        processingStateFlag.value = true

        try {
            getResponse(
                question,
                context,
                0,
                errorCallback = {
                    MainScope().launch {
                        navController.navigate(Paths.Error.Path+"/"+it)
                    }
                }
            ) { it ->
                var received = SavedRecipe()
                try {
                    received = SavedRecipe(QandA(Query(recipe), it, Json{ignoreUnknownKeys = true}.decodeFromString<ParsedResponse>(it.choices[0].message.content)))
                    Log.e("id before", received.id.toString())
                    received.id = saveID
                    recipeBox.put(received)

                    MainScope().launch {
                        val backStack = navController.currentBackStack.first()
                        val lastPage = backStack[backStack.size-1]
                        navController.navigate(Paths.Recipe.Path + "/" + received.id){
                            popUpTo(lastPage.destination.route?:"Search"){inclusive = true}
                        }
                    }
                } catch (e: Exception) {
                    MainScope().launch {
                        navController.navigate(Paths.Error.Path + "/" + it.choices[0].message.content)
                    }
                }
            }
        }catch (e: Exception) {
            MainScope().launch {
                navController.navigate(Paths.Error.Path + "/" + e)
            }
        }
    }



    fun presetRequest(promptText:String, presetId:Long, modifiers: SnapshotStateList<String>?):String{
        val thisPreset = presetBox[presetId]
        val constructedExclusions = thisPreset.enabledCarb + thisPreset.enabledMeat +
                thisPreset.excludedIngredients

        var request = "Give me a "
        when(thisPreset.meatContent){
            1 -> request+="Vegetarian "
            2 -> request+="Vegan "
            else->Unit
        }
        request += "recipe for $promptText. "
        if (constructedExclusions.isNotEmpty()) {
            request+= "Exclude the following ingredients: $constructedExclusions If an ingredient" +
                    " is important, provide the best alternative you can. "
        }
        if (thisPreset.descriptiveTags.isNotEmpty()) {
            request += "The recipe should fit the following descriptors: ${thisPreset.descriptiveTags}."
        }
        if (modifiers?.isNotEmpty() == true){
            request += "apply the following modifiers to the recipe: "
            modifiers.forEach { s -> request += "$s, " }
        }
        request+="[fin]"

        return request
    }

    fun generateQuestion(input: Query, modifiers: SnapshotStateList<String>):String{
        var question = "give me a recipe for a "
        var meatFlag = 0
        var carbFlag = 0
        if (input.cuisine!="(Optional)")question+="${input.cuisine} "
        when(input.meatContent){
            "Vegetarian" -> question+="Vegetarian "
            "Vegan" -> question+="Vegan "
            "Select..." -> return ""
            else ->{
                meatFlag =1
                if (input.primaryMeat=="Any")question+= "meat "
                else question += input.primaryMeat+ " "
            }
        }
        if(input.primaryCarb!="None") {

            if (input.primaryCarb == "Any") carbFlag = 1
            if (input.primaryCarb == "Other") carbFlag = 2
            if (meatFlag == 1&&carbFlag==0) question += "and "
            if(carbFlag==0) question += input.primaryCarb + " "
        }
        question+="dish "
        if (carbFlag==1) question+= "with any carbohydrate "
        if (carbFlag==2) question+= "with an unconventional carbohydrate "
        if(input.primaryCarb=="None") question+= "with no carbohydrates "

        if (input.additionalIngredients.size>0||input.excludedIngredients.size>0||input.descriptiveTags.size>0)question+="that "
        if (input.additionalIngredients.size>0) {
            question += "includes the following ingredients: "
            input.additionalIngredients.forEach { s -> question += "$s, " }
        }
        if (input.additionalIngredients.size>0&&input.excludedIngredients.size>0)question+="; and "

        if (input.excludedIngredients.size>0) {
            question += "excludes the following ingredients: "
            input.excludedIngredients.forEach { s -> question += "$s, " }
        }
        if (input.additionalIngredients.size>0&&input.descriptiveTags.size>0)question+="; and "

        if (input.descriptiveTags.size>0) {
            question += "fits the following descriptors: "
            input.descriptiveTags.forEach { s -> question += "$s, " }
        }
        if(input.servingSizes!=Pair(0,0)){
            question+=". The recipe must make enough to serve"
            if (input.servingSizes.first>0) {
                question+= input.servingSizes.first.toString()+" adults"
                if (input.servingSizes.second>0) question+=" and"
            }
            if (input.servingSizes.second>0) {
                question+= input.servingSizes.second.toString()+" children"
            }
        }
        question += when(input.budget){
            1-> ". This dish should cost less than $20. Do not mention this cost anywhere in the recipe"
            2-> ". This dish should cost between $15 and $40. Do not mention this cost anywhere in the recipe"
            3-> ". This dish should cost more than $50. Do not mention this cost anywhere in the recipe"
            else -> ""
        }

        if (modifiers.isNotEmpty()) {
            question += "apply the following modifiers to the recipe: ."
            modifiers.forEach { s -> question += "$s, " }
        }
        
        question+="[fin]"


        return question
    }

    fun recipeQuestion(input:SavedRecipe, modifiers:SnapshotStateList<String>):String {
        Log.d("debug modifiers", modifiers.toString())
        modifiers.forEach { s -> Log.d("debug modifiers", s) }
        var question = "Regenerate the following recipe"
        if (modifiers.isNotEmpty()) {
            question += "and modify it in the following ways: "
            modifiers.forEach { s -> question += "$s, " }
        }
        question += "Give the new recipe an appropriate title." +
                "Title: ${input.title}. Description: ${input.description}."+
                "Ingredients: ${input.ingredients.map { "$it, " }}. " +
                "Method: ${input.method.map { "$it, " }}. " +
                "Notes: ${input.notes.map { "$it, " }}"
        return question
    }

}
