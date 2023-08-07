package com.noxapps.dinnerroulette3

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
import java.io.IOException
import java.util.concurrent.TimeUnit


fun getResponse(question: String, context: Context, flag:Int, callback: (GptResponse) -> Unit){
    val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()

    val apiKey= context.getString(R.string.api_Key)
    Log.e("key", apiKey)
    val url="https://api.openai.com/v1/chat/completions"

    val prompt = generatePrompt(context, flag)
    Log.e("system", prompt)

    val requestBody="""
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
            Log.e("error","API failed",e)
        }

        override fun onResponse(call: Call, response: Response) {
            val body=response.body?.string()
            if (body != null) {
                Log.v("data",body)
            }
            else{
                Log.v("data","empty")
            }
            val output = body?.let { Json.decodeFromString<GptResponse>(it) }
            //val jsonObject= JSONObject(body)
            //val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
            //val textResult=jsonArray.getJSONObject(0).getString("message")
            callback(output!!)
        }
    })
}

fun getImage(prompt: String, context: Context, flag:Int, callback: (GptImageResponse) -> Unit){
    val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()

    val apiKey= context.getString(R.string.api_Key)
    Log.e("key", apiKey)
    val url="https://api.openai.com/v1/images/generations"

    //val prompt = generatePrompt(context, flag)
    Log.e("system", prompt)

    val requestBody="""
            {
            "prompt": "$prompt",
            "n": 1,
            "size": "1024x1024"
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
            Log.e("error","API failed",e)
        }

        override fun onResponse(call: Call, response: Response) {
            val body=response.body?.string()
            if (body != null) {
                Log.v("data",body)
            }
            else{
                Log.v("data","empty")
            }
            val output = body?.let { Json.decodeFromString<GptImageResponse>(it) }
            //val jsonObject= JSONObject(body)
            //val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
            //val textResult=jsonArray.getJSONObject(0).getString("message")
            callback(output!!)
        }
    })
}
fun parseResponse(gptResponse: GptResponse, flag:Int = 0):ParsedResponse{
    val initialText = gptResponse.choices[0].message.content
    val title = initialText.split("[title]", "[desc]")[1]
    val description = initialText.split("[desc]", "[ingredients]")[1]
    val ingredients = initialText.split("[ingredients]", "[method]")[1]
    val method = initialText.split("[method]", "[notes]")[1]
    if(flag==1){
        val notes = initialText.split("[notes]", "[image]")[1]
        val image = initialText.split("[image]")[1]
        return ParsedResponse(title.trim(), description.trim(), ingredients.trim(), method.trim(), notes.trim(), image.trim())
    }
    else{
        val notes = initialText.split("[notes]", "[image]")[1]
        return ParsedResponse(title.trim(), description.trim(), ingredients.trim(), method.trim(), notes.trim(), "")
    }
}

fun generatePrompt(context:Context, flag:Int):String{
    var imperial=false
    var fahrenheit = false
    val allergens = mutableListOf<String>()
    var skill = 0
    val loadedData = runBlocking { context.dataStore.data.first() }
    loadedData[savedPreferences]?.let{
        Log.d("saved preferences", it.toString())
        var retrievedData:Settings = try {
            Json.decodeFromString<Settings>(it)
        }catch(exception: MissingFieldException){
            Settings(false, false, listOf(), 0)
        }
        imperial=retrievedData.imperial
        fahrenheit=retrievedData.fahrenheit
        skill = retrievedData.skill
        retrievedData.allergens.forEach(){ allergen->
            if(!allergens.contains(allergen))allergens.add(allergen)
        }
    }
    var skillText="a beginner"
    when(skill){
        1-> skillText="an intermediate"
        2-> skillText="an expert"
    }

    var allergenText = "."
    if(allergens.size>0){
        allergenText=" who is algergic to or intolerant of the following: "
        allergens.forEach { allergenText+="$it, " }
        allergenText+="."
    }
    var unit1Text = "metric"
    if (imperial)unit1Text="imperial"

    var unit2Text = "celsius"
    if (fahrenheit)unit2Text = "fahrenheit"

    val prompt=when(flag){
        1-> "You are a recipe generating bot that receives a natural language prompt and returns a recipe suited to $skillText home cook$allergenText. The prompt will end with [fin], indicating the intended end of the prompt. you are to output a recipe in the format:[title]title of recipe [desc]brief description of recipe [ingredients]list of ingredients in $unit1Text units [method]recipe method with oven temperature displayed in $unit2Text [notes] optionally include any appropriate notes [image] a text description of the dish that will be used with dall-e to generate an accurate image of the dish"
        else-> "You are a recipe generating bot that receives a natural language prompt and returns a recipe suited to $skillText home cook$allergenText. The prompt will end with [fin], indicating the intended end of the prompt. the prompt will include a primary protein and a primary carbohydrate. for example, if the prompt requests a 'chinese lamb dish', lamb is the primary protein. if the prompt includes additional sources of protein or carbohydrate include them both, but make the primary protein or carbohydrate more prominent. be sure to give the recipe an appropriate name. You are to output a recipe in the format:[title]title of recipe [desc]brief description of recipe [ingredients]list of ingredients in $unit1Text units [method]recipe method with oven temperature displayed in $unit2Text [notes] optionally include any appropriate notes"

    }

    return prompt

}