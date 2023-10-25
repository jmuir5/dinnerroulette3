package com.noxapps.dinnerroulette3.commons

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.recipe.SavedRecipe

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FavouriteButton(id:Long, modifier: Modifier = Modifier){
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val thisRecipe = recipeBox[id]
    val checkedState = remember { mutableStateOf(thisRecipe.favourite) }
    FloatingActionButton(

        onClick = {
            checkedState.value = !checkedState.value
            thisRecipe.favourite = !thisRecipe.favourite
            recipeBox.put(thisRecipe)
        }

    ) {
        IconToggleButton(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                thisRecipe.favourite = !thisRecipe.favourite
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

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FreeFavouriteButton(id:Long, modifier: Modifier = Modifier) {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val thisRecipe = recipeBox[id]
    val checkedState = remember { mutableStateOf(thisRecipe.favourite) }
    if(checkedState.value!=thisRecipe.favourite)checkedState.value=thisRecipe.favourite

    IconToggleButton(
        checked = checkedState.value,
        onCheckedChange = {
            checkedState.value = !checkedState.value
            thisRecipe.favourite = !thisRecipe.favourite
            recipeBox.put(thisRecipe)
        },
        modifier = modifier

    ) {
        val transition = updateTransition(checkedState.value)
        val tint by transition.animateColor(label = "iconColor") { isChecked ->
            if (isChecked) Color.Red else Color.White

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