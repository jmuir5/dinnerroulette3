package com.noxapps.dinnerroulette3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import io.objectbox.Box
import io.objectbox.query.QueryCondition
import java.io.File

class SearchViewModel:ViewModel() {

    var tilesPerRow =3
    var screenWidth = 0

    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val allRecipes = recipeBox.all
    fun getImageOrPlaceholder(imageName:String?, context: Context): Bitmap {
        return if(isImageValid(imageName, context)){
            val currentFile =  File(context.filesDir, imageName)
            val filePath = currentFile.path
            BitmapFactory.decodeFile(filePath)
        } else{
            BitmapFactory.decodeResource(context.resources, R.drawable.placeholder_small)
        }
    }

    fun isImageValid(imageName:String?, context: Context) :Boolean{
        return if (imageName.isNullOrBlank()) false
        else File(context.filesDir, imageName).exists()


    }

    fun permuteList(
        recipeBox: Box<SavedRecipe>,
        searchLoc:Int,
        searchText:String,
        sortMethod:Int,
        filterBools:MutableList<MutableState<Boolean>>
        ):List<SavedRecipe> {

        val searchLocQuery = when(searchLoc){
            1->SavedRecipe_.title.contains(searchText)
            2->SavedRecipe_.ingredients.contains(searchText)
            3->SavedRecipe_.cuisine.contains(searchText)
            4->SavedRecipe_.description.contains(searchText)
            5->SavedRecipe_.method.contains(searchText)
            6->SavedRecipe_.notes.contains(searchText)
            else->SavedRecipe_.title.contains(searchText)
                .or(SavedRecipe_.ingredients.contains(searchText))
                .or(SavedRecipe_.cuisine.contains(searchText))
                .or(SavedRecipe_.description.contains(searchText))
                .or(SavedRecipe_.method.contains(searchText))
                .or(SavedRecipe_.notes.contains(searchText))
        }
        val filters = mutableListOf<QueryCondition<SavedRecipe>>()
        filterBools.forEachIndexed { index, b ->
            if(b.value){
                when(index){
                    0->filters.add(SavedRecipe_.favourite.equal(true))
                    1->filters.add(SavedRecipe_.meatContent.equal("Vegetarian").or(SavedRecipe_.meatContent.equal("Vegan")))
                    2->filters.add(SavedRecipe_.meatContent.equal("Vegan"))
                    else->Unit
                }
            }
        }
        var combinedCriteria =searchLocQuery
        filters.forEach{
            combinedCriteria=combinedCriteria.and(it)
        }
        var query = recipeBox.query(combinedCriteria)
        if(sortMethod>2) query = query.order(SavedRecipe_.title)
        var finalList = query.build().find()
        return if(sortMethod==0||sortMethod==3) finalList.reversed()
        else finalList

    }
}
