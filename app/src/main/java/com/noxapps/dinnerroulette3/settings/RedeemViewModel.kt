package com.noxapps.dinnerroulette3.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.code1State
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.gpt.getImage
import com.noxapps.dinnerroulette3.input.SettingsObject
import com.noxapps.dinnerroulette3.savedPreferences
import io.objectbox.Box
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RedeemViewModel: ViewModel() {
    //val presetBox: Box<DietPreset> = ObjectBox.store.boxFor(DietPreset::class.java)
    fun validateCode(
        code:String,
        title:MutableState<String>,
        body: MutableState<String>,
        processingState:MutableState<Boolean>,
        dialogueState:MutableState<Boolean>,
        context: Context,
        scope: CoroutineScope
    ){
        Log.d("code", code)
        if(code=="dev//:czarsevetsW4QKa"){
            Log.d("redeemFlag", "success")
            processingState.value = true
            //functional code goes here
            val loadedData = runBlocking { context.dataStore.data.first() }

            loadedData[savedPreferences]?.let {
                val retrievedData: SettingsObject = try {
                    Json.decodeFromString<SettingsObject>(it)
                }catch(exception: Exception){
                    SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
                }
                retrievedData.imageCredits+=1000
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[savedPreferences] = Json.encodeToString(retrievedData)
                    }
                }
            }
            title.value = "big money:"
            body.value = "BIG MONEY BIG PAPA"
            processingState.value = false
            dialogueState.value = true
        }
        else if(code=="dev//:5FreePics") {
            Log.d("redeemFlag", "success2")
            processingState.value = true
            //functional code goes here
            val loadedData = runBlocking { context.dataStore.data.first() }
            Log.d("redeemFlag", loadedData[code1State].toString())
            if (loadedData[code1State] == null) {
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[code1State] = true
                    }
                }
            }

            loadedData[code1State]?.let {
                val codeState: Boolean = try {
                    it
                } catch (exception: Exception) {
                    false
                }
                Log.d("redeemFlag", "state read")
                if (!codeState) {
                    Log.d("redeemFlag", "state read as false")
                    loadedData[savedPreferences]?.let {
                        val retrievedData: SettingsObject = try {
                            Json.decodeFromString<SettingsObject>(it)
                        } catch (exception: Exception) {
                            SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
                        }
                        Log.d("redeemFlag", "data loaded")
                        retrievedData.imageCredits += 5
                        scope.launch {
                            context.dataStore.edit { settings ->
                                settings[savedPreferences] = Json.encodeToString(retrievedData)
                                settings[code1State] = true
                            }
                        }
                    }
                    title.value = "5 Free Image Credits:"
                    body.value =
                        "You have been granted 5 free image credits! this code is one time use."
                    processingState.value = false
                    dialogueState.value = true
                } else {
                    Log.d("redeemFlag", "code read as used")
                    title.value = "Unsuccessful:"
                    body.value = "You have have already used this code."
                    processingState.value = false
                    dialogueState.value = true
                }
            }/*?.run {
                title.value = "Unsuccessful:"
                body.value = "an error has occoured."
                processingState.value = false
                dialogueState.value = true
            }*/


        }
        /*else if(code=="token"){
            val prompt = "create a realistic 3d render of a slightly tilted towards the camera gold coin featuring a generic icon representing images embossed in the center. place the coin on a circular gradient background that transitions smoothly from pale orange around the edges of the image to 0xFFFF861A in the center behind the coin."
            getImage(prompt, context){}
        }

         */


        else{
            Log.d("redeemFlag", "fail")
            processingState.value = true
            //functional code goes here
            title.value = "Unsuccessful"
            body.value = "The code you entered was incorrect"
            processingState.value = false
            dialogueState.value = true
        }
    }
}