package com.noxapps.dinnerroulette3.commons

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.noxapps.dinnerroulette3.adFlag
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.imageCredits
import com.noxapps.dinnerroulette3.purchaseFlag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun getAdFlag(context: Context):Boolean {
    val loadedData = runBlocking { context.dataStore.data.first() }
    loadedData[adFlag]?.let { flag ->
        return try {
            flag
        } catch (exception: Exception) {
            true
        }
    }
    return true
}
fun getImageCredits(context: Context):Int {
    val loadedData = runBlocking { context.dataStore.data.first() }
    loadedData[imageCredits]?.let { credits ->
        return try {
            credits
        } catch (exception: Exception) {
            0
        }
    }
    return 0
}

fun addImageCredits(context: Context, add:Int, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {
    Log.d("debug-credits add ", "add requested: $add")
    val loadedData = runBlocking { context.dataStore.data.first() }
    Log.d("debug-credits add ", "data loaded")

    loadedData[imageCredits]?.let { _credits ->
        Log.d("debug-credits add ", "add requested: $add")

        val credits =  try {
            _credits
        } catch (exception: Exception) {
            0
        }
        Log.d("debug-credits add ",credits.toString())
        scope.launch {
            context.dataStore.edit { settings ->
                Log.d("debug-credits add ",(credits+add).toString())
                settings[imageCredits] = credits+add
            }
        }
    }

}

fun getPurchaseFlag(context: Context):Int {
    val loadedData = runBlocking { context.dataStore.data.first() }
    loadedData[purchaseFlag]?.let { flag ->
        return try {
            flag
        } catch (exception: Exception) {
            -1
        }
    }
    return -1
}

fun setPurchaseFlag(context: Context, value:Int, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {
    scope.launch {
        context.dataStore.edit { settings ->
            settings[purchaseFlag] = value
        }
    }
}