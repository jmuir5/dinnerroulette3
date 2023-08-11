package com.noxapps.dinnerroulette3

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.noxapps.dinnerroulette3.ui.theme.AppTheme
import java.io.File

@Composable
fun SearchPage(
    navController: NavHostController,
    viewModel: SearchViewModel = SearchViewModel()
) {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val allRecipes by remember{ mutableStateOf(recipeBox.all)}
    viewModel.screenWidth = LocalConfiguration.current.screenWidthDp



    DrawerAndScaffold(tabt = "View Recipes", navController = navController) {
        RecipeList(allRecipes, navController, viewModel)
    }

}

@Composable
fun RecipeList(recipesList:List<SavedRecipe>, navController: NavHostController, viewModel: SearchViewModel){

    LazyColumn{
        for (i in recipesList.indices step(3)){
            item(){
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ){
                    RecipeCard(recipe = recipesList[i], navController = navController, viewModel)
                    if(i+1<recipesList.size-1){
                        RecipeCard(recipe = recipesList[i+1], navController = navController, viewModel)
                    }
                    if(i+2<recipesList.size-1){
                        RecipeCard(recipe = recipesList[i+2], navController = navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: SavedRecipe, navController: NavHostController, viewModel: SearchViewModel){
    val bitmap = viewModel.getImageOrPlaceholder(recipe.image, LocalContext.current)
    Column(
        modifier = Modifier
            .width((viewModel.screenWidth/viewModel.tilesPerRow).dp)
            .padding(8.dp)
            .clickable { navController.navigate(Paths.Recipe.Path + "/" + recipe.id) }
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ){
        Box(

        ){
            Image(
                painter = BitmapPainter(
                    image = bitmap.asImageBitmap()
                ),
                contentDescription = recipe.title,
                modifier = Modifier
                    //.aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                    .size(((viewModel.screenWidth/viewModel.tilesPerRow)-16).dp)

                ,
                contentScale = ContentScale.Fit
            )
        }
        Row(){
            Text(
                text = recipe.title!!,
                style = MaterialTheme.typography.labelMedium,
                minLines = 3,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }


}

