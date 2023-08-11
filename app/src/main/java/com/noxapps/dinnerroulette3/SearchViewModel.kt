package com.noxapps.dinnerroulette3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import java.io.File

class SearchViewModel:ViewModel() {

    var tilesPerRow =3
    var screenWidth = 0
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
}
