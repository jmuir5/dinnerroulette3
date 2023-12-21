package com.noxapps.dinnerroulette3.gpt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.input.ParsedResponse
import com.noxapps.dinnerroulette3.settings.SettingsObject
import com.noxapps.dinnerroulette3.savedPreferences
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(300, TimeUnit.SECONDS)
    .writeTimeout(300, TimeUnit.SECONDS)
    .readTimeout(300, TimeUnit.SECONDS)
    .build()

/**
 * file containing common functions used for communicating with chat gpt
 */

/**
 * function to get a text recipe response from chat gpt.
 * [question]: question to ask chat gpt in the form of a string
 * [context]: context (LocalContext.Current, usually)
 * [flag]: int flag for use with generatePrompt() indicating source/version of prompt that needs tobe generated
 * [callback] : GptResponse returned to parent function
 */
fun getResponse(question: String, context: Context, flag: Int, callback: (GptResponse) -> Unit) {

    val apiKey = context.getString(R.string.api_Key)
    Log.e("key", apiKey)
    val url = "https://api.openai.com/v1/chat/completions"

    val prompt = generatePrompt(context, flag)
    Log.e("system", prompt)

    val requestBody = """
            {
            "model": "gpt-3.5-turbo",
            "messages": [{"role": "system", "content": "$prompt"},{"role": "user", "content":"$question"}]
            }
        """.trimIndent()
    Log.e("request body", requestBody)

    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("error", "API failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (body != null) {
                Log.v("data", body)
            } else {
                Log.v("data", "empty")
            }
            val output = body?.let { Json{ignoreUnknownKeys = true}.decodeFromString<GptResponse>(it) }
            //val jsonObject= JSONObject(body)
            //val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
            //val textResult=jsonArray.getJSONObject(0).getString("message")
            callback(output!!)
        }
    })
}

/**
 * function to get a image response from chat gpt.
 * [prompt]: prompt used to generate image from dall-e
 * [context]: context (LocalContext.Current, usually)
 * [callback] : GptImageResponse returned to parent function
 */
fun getImage(prompt: String, context: Context, callback: (GptImageResponse) -> Unit) {

    val apiKey = context.getString(R.string.api_Key)
    Log.e("key", apiKey)
    val url = "https://api.openai.com/v1/images/generations"

    //val prompt = generatePrompt(context, flag)
    Log.e("system", prompt)

    val requestBody = """
            {
            "prompt": "$prompt",
            "n": 1,
            "size": "256x256"
            }
        """.trimIndent()
    Log.e("request body", requestBody)

    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("error", "API failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (body != null) {
                Log.v("data", body)
            } else {
                Log.v("data", "empty")
            }
            val output = body?.let { Json{ignoreUnknownKeys = true}.decodeFromString<GptImageResponse>(it) }
            //val jsonObject= JSONObject(body)
            //val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
            //val textResult=jsonArray.getJSONObject(0).getString("message")
            callback(output!!)
        }
    })
}

/**
 * parse response: function to take a chat gpt response (not json,not perfectly consistant, not able
 * to remove header, therefore not able to use serialisation library) and convert it into a parsed
 * response, effectively a container of strings denoting title, method, etc in an easily usable
 * format.
 * [gptResponse]: source response to decode
 */
fun parseResponse(gptResponse: GptResponse): ParsedResponse {
    val initialText = gptResponse.choices[0].message.content
    val title = initialText.split("[title]", "[desc]")[1]
    val description = initialText.split("[desc]", "[ingredients]")[1]
    val ingredients = initialText.split("[ingredients]", "[method]")[1]
    val method = initialText.split("[method]", "[notes]")[1]
    val notes = initialText.split("[notes]", "[image]")[1]
    val image = initialText.split("[image]")[1]
    return ParsedResponse(
        title = title.trim(),
        description = description.trim(),
        ingredients = ingredients.trim(),
        method = method.trim(),
        notes = notes.trim(),
        image = image.trim()
    )
}

/**
 * function to generate the prompt for chat gpt from user settings and standard prompt components,
 * adjusted depending on the source of the request (random, specific recipe, tailor made, deliniated
 * with [flag])
 */
@OptIn(ExperimentalSerializationApi::class)
fun generatePrompt(context: Context, flag: Int): String {
    var imperial = false
    var fahrenheit = false
    var skill = 0

    var meatContent = 0
    val loadedData = runBlocking { context.dataStore.data.first() }
    val retrievedData: SettingsObject = try {
        Json.decodeFromString<SettingsObject>(loadedData[savedPreferences]!!)
    } catch (exception: MissingFieldException) {
        SettingsObject(
            imperial = false,
            fahrenheit = false,
            skill = 0,
            dietPreset = 0,
            budget = 0,
        )
    }
    imperial = retrievedData.imperial
    fahrenheit = retrievedData.fahrenheit
    skill = retrievedData.skill



    var skillText = ""
    when (skill) {
        1-> skillText = "a beginner"
        2 -> skillText = "an intermediate"
        3 -> skillText = "an expert"
    }


    var unit1Text = "metric"
    if (imperial) unit1Text = "imperial"

    var unit2Text = "celsius"
    if (fahrenheit) unit2Text = "fahrenheit"


    val prompt = when (flag) {
        1 -> "You are a recipe generating bot that receives a natural language prompt and returns a recipe suited to $skillText home cook. The prompt will end with [fin], indicating the intended end of the prompt. include a recommendation for an appropriate carbohydrate component or accompaniment in the description. you are to output a recipe in the format:[title]title of recipe [desc]brief description of recipe [ingredients]list of ingredients in $unit1Text units [method]recipe method with oven temperature displayed in $unit2Text [notes] optionally include any appropriate notes [image] a text description of the dish that will be used with dall-e to generate an accurate image of the dish"
        else -> "You are a recipe generating bot that receives a natural language prompt and returns a recipe suited to $skillText home cook. The prompt will end with [fin], indicating the intended end of the prompt. the prompt will include a primary protein and a primary carbohydrate. for example, if the prompt requests a 'chinese lamb dish', lamb is the primary protein. if the prompt includes additional sources of protein or carbohydrate include them both, but make the primary protein or carbohydrate more prominent. be sure to give the recipe an appropriate name. You are to output a recipe in the format:[title]title of recipe [desc]brief description of recipe [ingredients]list of ingredients in $unit1Text units [method]recipe method with oven temperature displayed in $unit2Text [notes] optionally include any appropriate notes [image] a text description of the dish that will be used with dall-e to generate an accurate image of the dish"

    }

    return prompt

}

/**
 * function to download an image from the internet, and save it to the image variable of a
 * savedRecipe. this is required as images generated by dalle are only available for a few hours,
 * and i do not want to host any data off the users phone so this function permanantly saves
 * images.
 * [context]: context
 * [savedRecipe]: savedRecipe object to save local image location to
 * [imageUrl]: string representation of the image url to be saved
 * [callback]: returns whether or not the function succeds or fails. perhaps theres a better way to do this?
 */
fun saveImage(
    context: Context,
    savedRecipe: SavedRecipe,
    imageUrl: String,
    callback: (Boolean) -> Unit
) {
    val name = savedRecipe.title?.replace(" ", "_") + LocalDateTime.now().toString()
    val currentFile = File(context.filesDir, name)
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)


    val url = URL(imageUrl)//url
    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
    currentFile.outputStream().use {
        image.compress(Bitmap.CompressFormat.PNG, 100, it)

        savedRecipe.image = name
        recipeBox.put(savedRecipe)
        callback(true)
    }
    image.recycle()


}

