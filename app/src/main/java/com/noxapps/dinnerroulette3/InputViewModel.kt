package com.noxapps.dinnerroulette3

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.MissingFieldException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class InputViewModel: ViewModel() {
    init{}
    private val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()

    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    fun generateQuestion(input:Query):String{
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
        question+="[fin]"


        return question
    }

    fun generateSystem(context:Context):String{
        var imperial=false
        var fahrenheit = false
        val allergens = mutableListOf<String>()
        var skill = 0
        val loadedData = runBlocking { context.dataStore.data.first() }
        loadedData[savedPreferences]?.let{
            Log.d("saved preferences", it.toString())
            var retrievedData:Settings
            try {
                retrievedData = Json.decodeFromString<Settings>(it)
            }catch(exception: MissingFieldException){
                retrievedData = Settings(false, false, listOf(), 0)
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

        val prompt="You are a recipe generating bot that receives a natural language prompt and returns a recipe suited to $skillText home cook$allergenText The prompt will end with [fin], indicating the intended end of the prompt. the prompt will include a primary protein and a primary carbohydrate. for example, if the prompt requests a 'chinese lamb dish', lamb is the primary prot ,mmein. if the prompt includes additional sources of protein or carbohydrate include them both, but make the primary protein or carbohydrate more prominent.  you are to output a recipe in the format:[title]title of recipe [desc]brief description of recipe [ingredients]list of ingredients in $unit1Text units [method]recipe method with oven temperature displayed in $unit2Text [notes] optionally include any appropriate notes"

        return prompt

    }

    //https://platform.openai.com/docs/api-reference/making-requests
    fun getResponse(question: String, context:Context, callback: (GptResponse) -> Unit){
        val apiKey= context.getString(R.string.api_Key)
        Log.e("key", apiKey)
        val url="https://api.openai.com/v1/chat/completions"

        val prompt = generateSystem(context)
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

    fun parseResponse(gptResponse: GptResponse):ParsedResponse{
        val initialText = gptResponse.choices[0].message.content
        val title = initialText.split("[title]", "[desc]")[1]
        val description = initialText.split("[desc]", "[ingredients]")[1]
        val ingredients = initialText.split("[ingredients]", "[method]")[1]
        val method = initialText.split("[method]", "[notes]")[1]
        val notes = initialText.split("[notes]")[1]
        return ParsedResponse(title, description, ingredients, method, notes)
    }

}