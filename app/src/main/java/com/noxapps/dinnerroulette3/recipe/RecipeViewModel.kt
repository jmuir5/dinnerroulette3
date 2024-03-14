package com.noxapps.dinnerroulette3.recipe

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.storage.storage
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.gpt.saveImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class RecipeViewModel: ViewModel() {
    val database = Firebase.database.reference
    val storage = Firebase.storage.reference

    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)

    suspend fun share(
        recipe:SavedRecipe,
        processingState:MutableState<Boolean>,
        context: Context
    ){
        if(!recipe.shareUrl.isNullOrEmpty()){
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                recipe.shareUrl
            )
                .putExtra(Intent.EXTRA_TITLE, recipe.title)
            intent.type = "text/plain"
            startActivity(context, intent, Bundle.EMPTY)
        }
        else {
            MainScope().launch { processingState.value = true }
            val newRecipe = uploadImage(recipe, context)
            database.child("nextId").get()
                .addOnSuccessListener {
                    if (it.value is Long) {
                        newRecipe.id = it.value as Long

                        recipe.shareUrl =
                            "https://www.chefroulette.com.au/recipe?id=${newRecipe.id}"
                        recipeBox.put(recipe)

                        newRecipe.shareUrl =
                            "https://www.chefroulette.com.au/recipe?id=${newRecipe.id}"
                        database.child("Recipes").child(newRecipe.id.toString()).setValue(newRecipe)
                        database.child("nextId").setValue(newRecipe.id + 1)
                        MainScope().launch {
                            processingState.value = false

                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.putExtra(
                                Intent.EXTRA_TEXT,
                                "https://www.chefroulette.com.au/recipe?id=${newRecipe.id}"
                            )
                                .putExtra(Intent.EXTRA_TITLE, newRecipe.title)
                            intent.type = "text/plain"
                            startActivity(context, intent, Bundle.EMPTY)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("error sharing recipe", it.toString())
                    MainScope().launch { processingState.value = false }
                }
        }
    }

    suspend fun uploadImage(
        recipe:SavedRecipe,
        context: Context
    ):SavedRecipe = withContext(Dispatchers.IO) {
        if (recipe.image.isNullOrEmpty()) return@withContext recipe
        val file = Uri.fromFile(File(context.filesDir,recipe.image!!))
        val imageRef = storage.child("Images/${recipe.image!!}")
        val uploadTask = imageRef.putFile(file)
        uploadTask.addOnFailureListener {
            Log.e("upload task", "upload task failed, set to blank")
            recipe.image = ""
        }.addOnSuccessListener {
            Log.e("upload task", "upload task successfull")
        }
        uploadTask.await()
        val uriTask = imageRef.downloadUrl
        uriTask.addOnFailureListener{
            Log.e("uri task", "uri task failed, set to blank")
            recipe.image = ""
        }.addOnSuccessListener {
            Log.e("upload task", "upload task successfull")
            recipe.image = uriTask.result.toString()
        }
        uriTask.await()

        return@withContext recipe
    }

    fun getRecipe(
        id:Long,
        recipe:MutableState<SavedRecipe>,
        processingState: MutableState<Boolean>,
        imageFlag: MutableState<Boolean>,
        imageFlag2: MutableState<Boolean>,
        context: Context,
        coroutineScope:CoroutineScope
    ){
        val recipeTask = database.child("Recipes").child(id.toString()).get()
        recipeTask.addOnFailureListener{
            Log.e("recipe task", "recipe task failed")
        }.addOnSuccessListener {
            Log.e("recipe task", "recipe task succeded, ${it.value}")

            val receivedRecipe =  it.getValue<SavedRecipe>()
            if(receivedRecipe!=null) {
                recipe.value = receivedRecipe
                processingState.value = false
                saveIfNew(receivedRecipe, imageFlag,imageFlag2, context, coroutineScope)
            }
        }
    }

    fun saveIfNew(
        recipe:SavedRecipe,
        imageFlag: MutableState<Boolean>,
        imageFlag2: MutableState<Boolean>,
        context: Context,
        coroutineScope:CoroutineScope
    ) {
        recipe.shareUrl?.let {
            val query = recipeBox.query(SavedRecipe_.shareUrl.equal(it)).build()
            val matchingRecipes = query.find()
            if (matchingRecipes.isEmpty()) {
                recipe.id = 0
                if (!recipe.image.isNullOrEmpty()){
                    val imgUrl = recipe.image!!
                    imageFlag.value = true
                    coroutineScope.launch {
                        withContext(Dispatchers.IO){
                            saveImage(context,recipe, imgUrl){it2 ->
                                MainScope().launch { imageFlag2.value = it2}
                            }
                        }
                    }
                }
                recipeBox.put(recipe)
            }
        }
    }
}