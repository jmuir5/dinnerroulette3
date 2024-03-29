package com.noxapps.dinnerroulette3.home

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.InterstitialAdDialogue
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import com.noxapps.dinnerroulette3.commons.ProcessingDialog
import com.noxapps.dinnerroulette3.commons.UpdateDialog
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.loadInterstitialAd

/**
 * home page composable. needs a total redesign based on ui paradigms
 */
@Composable
fun HomePage(
    viewModel: HomeViewModel = HomeViewModel(),
    navController: NavHostController,
) {
    val context = LocalContext.current
    val adFlag = getAdFlag(context)
    StandardScaffold("Chef Roulette",navController, homePageFlag = true, adFlag = adFlag) {
        val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
        val processing = remember { mutableStateOf(false) }

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.roulette_table)
        val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap())}

        val adFrameFlag = remember { mutableStateOf(false) }
        val adReference = if(BuildConfig.DEBUG){
            LocalContext.current.getString(R.string.test_roulette_interstitial_ad_id)
        }
        else LocalContext.current.getString(R.string.roulette_interstitial_ad_id)

        val updatePrimed = remember{mutableStateOf(true)}
        val upToDateState by remember{ derivedStateOf { (viewModel.buildVersion<viewModel.upToDateVersion.intValue)&&updatePrimed.value }}

        loadInterstitialAd(
            context,
            viewModel.mInterstitialAd,
            "Home Page Interstitial",
            adReference
        )
        Log.d("Adload","home page load attempted")
        if (viewModel.mInterstitialAd.value == null) {

        }



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
                        contentDescription = "ChefRoulette",
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
                            try {
                                if(recipeBox.all.size<2||!adFlag){
                                    viewModel.executeRandom(processing, context, navController)
                                }
                                else {
                                    adFrameFlag.value = true
                                }
                            }
                            catch (e:Exception){
                                navController.navigate(Paths.Error.Path+"/${e}")
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Play Chef\nRoulette",
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
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                ){
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
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
            ProcessingDialog("Currently generating your random recipe")
        }
        if(adFrameFlag.value){
            InterstitialAdDialogue(
                mInterstitialAd = viewModel.mInterstitialAd,
                context = context,
                displayFlag = adFrameFlag,
                function = {
                    viewModel.executeRandom(processing, context, navController)
                }
            )
        }
        if (upToDateState) {
            UpdateDialog(state = updatePrimed)
        }
    }
}

