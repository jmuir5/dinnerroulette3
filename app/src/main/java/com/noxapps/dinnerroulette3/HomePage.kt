package com.noxapps.dinnerroulette3

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * home page composable. needs a total redesign based on ui paradigms
 */
@Composable
fun HomePage(
    viewModel: HomeViewModel = HomeViewModel(),
    navController: NavHostController,
) {
    DrawerAndScaffold("Dinner Roulette",navController) {
        val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
        val processing = remember { mutableStateOf(false) }
        val context = LocalContext.current

        val genState = remember { mutableStateOf(true) }
        val savedState = remember { mutableStateOf(false) }
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.roulette_table)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap())}

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Spacer(modifier = Modifier
                    .weight(0.5f))
                Box(
                    Modifier
                        .weight(5f),
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painter,
                        contentDescription = "DinnerRoulette",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height),
                        onClick = {
                            viewModel.executeRandom(processing, context, navController)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Play Dinner\nRoulette",
                            style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                ){
                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(5f)
                            .padding(0.dp, 12.dp, 12.dp, 12.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            navController.navigate(Paths.SpecificRecipeInput.Path)
                        }) {
                        Text(text = "Request A Recipe By Name",
                            style = MaterialTheme.typography.headlineSmall)
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(5f)
                            .padding(0.dp, 12.dp, 12.dp, 12.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            navController.navigate(Paths.NewInput.Path)
                        }) {
                        Text(text = "Build A Custom Recipe",
                            style = MaterialTheme.typography.headlineSmall)
                    }
                }

                    /*Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(5f),
                        onClick = {
                            navController.navigate(Paths.NatLanInput.Path)
                        }) {
                        Text(text = "New Input - nyi")
                    }*/



                //Spacer(modifier = Modifier.size(10.dp))


                Row (modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                ){
                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .fillMaxHeight()
                            .padding(0.dp, 12.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            navController.navigate(Paths.Search.Path)
                        }) {
                        Text(text = "Browse Saved Recipes",
                            style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
        if (processing.value) {
            ProcessingDialog()
        }
    }
}

/**
 * header card composable, requires total redesign, probably going to be removed in the future
 */
@Composable
fun HeadCard(state:MutableState<Boolean>, title:String){
    val height =if (state.value){65.dp}else{100.dp}
    val style = if (state.value){MaterialTheme.typography.titleLarge}else{MaterialTheme.typography.headlineLarge}
    //val color = if (state.value){MaterialTheme.colorScheme.primaryContainer}else{MaterialTheme.colorScheme
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .padding(0.dp, 8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(15.dp)
        )
        .clickable { state.value = !state.value },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge//style
        )
    }
}

/**
 * subheader card composable, requires total redesign, probably going to be removed in the future
 */

@Composable
fun subHeadCard(state:MutableState<Boolean>, title:String){
    val height =if (state.value){45.dp}else{80.dp}
    val style = if (state.value){MaterialTheme.typography.titleLarge} else{MaterialTheme.typography.headlineLarge}
    //val color = if (state.value){MaterialTheme.colorScheme.primaryContainer}else{MaterialTheme.colorScheme
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .padding(8.dp)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(15.dp)
        )
        .clickable { state.value = !state.value },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        Text(
            text = title,
            style = style
        )
    }
}