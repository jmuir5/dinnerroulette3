package com.noxapps.dinnerroulette3

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import java.io.File

//import com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
//import com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange


@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun Recipe(
    recipeId:Long,
    TABT: MutableState<String>

) {

    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val thisRecipe = recipeBox[recipeId]
    val parsedIngredients = thisRecipe.ingredients?.split("\n")
    Log.e("image", thisRecipe.image.toString())

    TABT.value = thisRecipe.id!!.toString()
    Scaffold(
        modifier = Modifier.padding(24.dp, 0.dp),
        floatingActionButton = {
            FavouriteButton(recipeId)
        }
    ) { contentPadding ->
        Column (
            modifier = Modifier.verticalScroll(rememberScrollState()))
        {
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)){
                    Box(modifier = Modifier.align(Alignment.Center)){
                        Indicator()
                    }
                }
                if (thisRecipe.image?.isNotEmpty() == true) {
                    val currentFile = File(LocalContext.current.filesDir, thisRecipe.image)
                    val filePath = currentFile.path
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    Image(
                        painter = BitmapPainter(image = bitmap.asImageBitmap()),
                        contentDescription = thisRecipe.title,
                        modifier = Modifier.fillMaxWidth()
                    )
                    /*AsyncImage(
                        model = thisRecipe.image,
                        contentDescription = thisRecipe.title,
                        modifier = Modifier.fillMaxWidth()
                    )*/
                } else {
                }
                Text(
                    text = thisRecipe.title!!,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                //.background(SurfaceOrange)
            ) {
                Text(
                    text = thisRecipe.description!!,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.headlineLarge
                )
                parsedIngredients?.forEach() {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val checkedState = remember { mutableStateOf(false) }
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                        )
                        Text(
                            text = it,
                            modifier = Modifier
                                .weight(9f)
                                .clickable { checkedState.value = !checkedState.value },
                            style = if (checkedState.value) {
                                MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = TextDecoration.LineThrough
                                )
                            } else MaterialTheme.typography.bodyMedium
                        )

                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Method",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = thisRecipe.method!!,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = thisRecipe.notes!!,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(100.dp))

            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FavouriteButton(id:Long){
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val thisRecipe = recipeBox[id]
    val checkedState = remember { mutableStateOf(thisRecipe.favourite) }
    FloatingActionButton(

        onClick = {
            checkedState.value =!checkedState.value
            thisRecipe.favourite =!thisRecipe.favourite
            recipeBox.put(thisRecipe)
        }

    ) {
        IconToggleButton(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value =!checkedState.value
                thisRecipe.favourite =!thisRecipe.favourite
                recipeBox.put(thisRecipe)
            },
            modifier = Modifier
                //.background(PrimaryOrange)
                .padding(10.dp)
        ) {
            val transition = updateTransition(checkedState.value)
            val tint by transition.animateColor(label = "iconColor") { isChecked ->
                if (isChecked) Color.Red else Color.Black
            }
            val size by transition.animateDp(
                transitionSpec = {
                    // on below line we are specifying transition
                    if (false isTransitioningTo true) {
                        // on below line we are specifying key frames
                        keyframes {
                            // on below line we are specifying animation duration
                            durationMillis = 250
                            // on below line we are specifying animations.
                            30.dp at 0 with LinearOutSlowInEasing // for 0-15 ms
                            35.dp at 15 with FastOutLinearInEasing // for 15-75 ms
                            40.dp at 75 // ms
                            35.dp at 150 // ms
                        }
                    } else {
                        spring(stiffness = Spring.StiffnessVeryLow)
                    }
                },
                label = "Size"
            ) { 30.dp }
            Icon(
                // on below line we are specifying icon for our image vector.
                imageVector = if (checkedState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Icon",
                // on below line we are specifying
                // tint for our icon.
                tint = tint,
                // on below line we are specifying
                // size for our icon.
                modifier = Modifier.size(size)
            )
        }
    }
}

@Composable
fun imageFromFile(filename:String, context:Context){

}