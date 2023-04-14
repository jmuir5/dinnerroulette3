package com.noxapps.dinnerroulette3

import android.util.Log
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class optionsViewModel: ViewModel() {
    init{}
    private val client = OkHttpClient()

    fun generateQuestion(input:Query):String{
        var question = "give me a recipe for a "
        var meatFlag = 0
        var carbFlag = 0
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

        if (input.additionalIngredients.size>0||input.descriptiveTags.size>0)question+="that "
        if (input.additionalIngredients.size>0) {
            question += "includes the following ingredients: "
            input.additionalIngredients.forEach { s -> question += s + ", " }
        }
        if (input.additionalIngredients.size>0&&input.descriptiveTags.size>0)question+="; and "

        if (input.additionalIngredients.size>0) {
            question += "fits the following descriptors: "
            if (input.spiceContent>0){
                if (input.spiceContent==2)question+= "not "
                question+="spicy, "
            }
            if (input.cheeseContent>0){
                if (input.cheeseContent==2)question+= "not "
                question+="cheesy, "
            }
            if (input.glutenFree)question+="gluten free, "
            if (input.lactoseFree)question+="lactose free, "
            input.descriptiveTags.forEach { s -> question += s + ", " }
        }



        return question
    }

    //https://platform.openai.com/docs/api-reference/making-requests
    fun getResponse(question: String, callback: (String) -> Unit){
        val apiKey="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"
        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"

        val requestBody="""
            {
            "prompt": "$question",
            "max_tokens": 1000,
            "temperature": 0.3
            }
        """.trimIndent()

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
                val jsonObject= JSONObject(body)
                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }

}