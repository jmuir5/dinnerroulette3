package com.noxapps.dinnerroulette3.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.adFlag
import com.noxapps.dinnerroulette3.code1State
import com.noxapps.dinnerroulette3.commons.addImageCredits
import com.noxapps.dinnerroulette3.commons.getImageCredits
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.gpt.getImage
import com.noxapps.dinnerroulette3.savedPreferences
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
        when (code) {
            context.getString(R.string.code_max_tokens) -> {
                Log.d("redeemFlag", "success")
                processingState.value = true
                //functional code goes here
                addImageCredits(context, 1000)
                title.value = "big money:"
                body.value = "BIG MONEY BIG PAPA"
                processingState.value = false
                dialogueState.value = true
            }
            context.getString(R.string.code_img_trial) -> {
                Log.d("redeemFlag", "success2")
                processingState.value = true
                //functional code goes here
                val loadedData = runBlocking { context.dataStore.data.first() }
                Log.d("redeemFlag", loadedData[code1State].toString())

                loadedData[code1State]?.let {
                    val codeState: Boolean = try {
                        it
                    } catch (exception: Exception) {
                        false
                    }
                    Log.d("redeemFlag", "state read")
                    if (!codeState) {
                        Log.d("redeemFlag", "state read as false")
                        addImageCredits(context, 5)
                        title.value = "5 Free Image Credits:"
                        body.value =
                            "You have been granted 5 free image credits! this code is one time use."
                        scope.launch {
                            context.dataStore.edit { settings ->
                                settings[code1State] = true
                            }
                        }
                        processingState.value = false
                        dialogueState.value = true
                    } else {
                        Log.d("redeemFlag", "code read as used")
                        title.value = "Unsuccessful:"
                        body.value = "You have have already used this code."
                        processingState.value = false
                        dialogueState.value = true
                    }
                }
            }
            context.getString(R.string.code_reset_img_credits) ->{
                Log.d("redeemFlag", "success")
                processingState.value = true
                //functional code goes here
                addImageCredits(context, -getImageCredits(context))
                title.value = "image credits reset:"
                body.value = "ZERO MONEY ZERO PAPA"
                processingState.value = false
                dialogueState.value = true
            }
            context.getString(R.string.code_reactivate_ads)->{
                Log.d("redeemFlag", "success")
                processingState.value = true
                //functional code goes here
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[adFlag] = true
                    }
                }
                title.value = "ad flag reset:"
                body.value = "ZERO MONEY ZERO PAPA (ads on)"
                processingState.value = false
                dialogueState.value = true
            }
            context.getString(R.string.code_remove_ads)->{
                Log.d("redeemFlag", "success")
                processingState.value = true
                //functional code goes here
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[adFlag] = false
                    }
                }
                title.value = "ads disabled:"
                body.value = "ZERO MONEY ZERO PAPA (ads off)"
                processingState.value = false
                dialogueState.value = true
            }
            /*"token"-> {
                val prompt =
                    "create a 3d render that conveys the meaning 'ad removal' or 'remove ads'"
                getImage(prompt, context) {}
            }*/
            else -> {
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
}