package com.noxapps.dinnerroulette3.recipe

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.google.android.gms.ads.rewarded.RewardedAd
import com.noxapps.dinnerroulette3.AdmobBanner
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.commons.Indicator
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.RewardedAdFrame
import com.noxapps.dinnerroulette3.code1State
import com.noxapps.dinnerroulette3.dataStore
import com.noxapps.dinnerroulette3.gpt.getImage
import com.noxapps.dinnerroulette3.gpt.saveImage
import com.noxapps.dinnerroulette3.input.SettingsObject
import com.noxapps.dinnerroulette3.loadRewardedAd
import com.noxapps.dinnerroulette3.savedPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun Recipe(
    recipeId:Long,
    navController: NavHostController

) {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    var thisRecipe = recipeBox[recipeId]

    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var consumed by remember { mutableStateOf(0f) }
    var columnHeightDp by remember { mutableStateOf(0f) }
    val imageHeight = with(LocalDensity.current) { screenWidth.roundToPx().toFloat() }
    val screenHeightPx = with(LocalDensity.current) { screenHeight.roundToPx().toFloat() }
    val downHeightPx = with(LocalDensity.current) { 110.dp.roundToPx().toFloat() }
    val upHeightPx = with(LocalDensity.current) { 64.dp.roundToPx().toFloat() }
    val state = scrollBehaviour.state


    val imageFlag = remember { mutableStateOf(thisRecipe.image!!.isNotEmpty()) }
    val imageFlag2 = remember { mutableStateOf(imageFlag.value) }

    val collapsedFraction by remember{ derivedStateOf { scrollBehaviour.state.collapsedFraction < 0.70 }}


    val topAppBarElementColor = remember {mutableStateOf(Color.Transparent)}
    val iconButtonBackgroundColor = remember {mutableStateOf(Color.Gray)}
    val iconColor = remember {mutableStateOf(Color.Black)}
    if (collapsedFraction) {
        topAppBarElementColor.value= Color.Transparent
        iconButtonBackgroundColor.value = Color.Gray
        iconColor.value = Color.Black
    } else {
        topAppBarElementColor.value=MaterialTheme.colorScheme.onPrimary
        iconButtonBackgroundColor.value =MaterialTheme.colorScheme.primary
        iconColor.value =MaterialTheme.colorScheme.onPrimary
    }


    val customScroll = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!imageFlag.value && !imageFlag2.value) return Offset.Zero
            consumed += available.y
            if (consumed >= 0) consumed = 0f
            if (consumed < -columnHeightDp + screenHeightPx) consumed =
                -columnHeightDp + screenHeightPx + 2
            if (consumed > -(imageHeight - (downHeightPx)) && available.y < 0) return Offset.Zero
            else if (consumed < -(imageHeight - (upHeightPx)) && available.y > 0) return Offset.Zero
            state.heightOffset = state.heightOffset + available.y
            return Offset.Zero
        }


    }
    Scaffold(
        modifier = Modifier
            .nestedScroll(customScroll),

        floatingActionButton = {
            FavouriteButton(recipeId)
        },
        topBar = {
            if (imageFlag.value || imageFlag2.value) {
                MediumTopAppBar(
                    title = {
                        Text(
                            text = thisRecipe.title!!,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehaviour,
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = topAppBarElementColor.value
                    ),

                    navigationIcon = {
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = iconButtonBackgroundColor.value,
                                contentColor = iconColor.value
                            ),
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = thisRecipe.title!!,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                )
            }
        }
    ) { contentPadding ->
        var variableModifier = if (imageFlag.value || imageFlag2.value) {
            Modifier
                .padding(0.dp)
                .verticalScroll(rememberScrollState())
                .onGloballyPositioned { coordinates ->
                    columnHeightDp = coordinates.size.height.toFloat()
                }
        } else {
            Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .onGloballyPositioned { coordinates ->
                    columnHeightDp = coordinates.size.height.toFloat()
                }
        }

        Column(
            modifier = variableModifier
        )
        {
            TitleCardFull(thisRecipe = thisRecipe, imageFlag = imageFlag, imageFlag2 = imageFlag2)
            RecipeBody(thisRecipe = thisRecipe)

        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FavouriteButton(id:Long, modifier:Modifier = Modifier){
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

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FreeFavouriteButton(id:Long, modifier:Modifier = Modifier) {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val thisRecipe = recipeBox[id]
    val checkedState = remember { mutableStateOf(thisRecipe.favourite) }
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

@Composable
fun RecipeBody(
    thisRecipe: SavedRecipe
) {
    val parsedIngredients = thisRecipe.ingredients?.split("\n")
    val parsedMethod = thisRecipe.method?.split("\n")
    val parsedNotes = thisRecipe.notes?.split("\n")
    val adReference = if(BuildConfig.DEBUG){
        LocalContext.current.getString(R.string.test_scaffold_banner_ad_id)
    }
    else LocalContext.current.getString(R.string.scaffold_banner_ad_id)
    Column(
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = thisRecipe.description!!,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(10.dp))
        Row(){
            Spacer(modifier = Modifier
                .height(50.dp))
            AdmobBanner(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
                reference = adReference
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        parsedIngredients?.forEach() {
            if(it.isEmpty()){
                Spacer(modifier=Modifier.size(10.dp))
            }
            else if(it.startsWith("-")) {
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
            else{
                Text(text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(){
            Spacer(modifier = Modifier
                .height(50.dp))
            AdmobBanner(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
                reference = adReference
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Method",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        parsedMethod?.forEach() {
            Row(modifier = Modifier
                .padding(0.dp, 8.dp),

                verticalAlignment = Alignment.CenterVertically) {
                val strikeThroughState = remember { mutableStateOf(false) }
                Text(
                    text = it,
                    modifier = Modifier
                        .clickable { strikeThroughState.value = !strikeThroughState.value },
                    style = if (strikeThroughState.value) {
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    } else MaterialTheme.typography.bodyMedium
                )

            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(){
            Spacer(modifier = Modifier
                .height(50.dp))
            AdmobBanner(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
                reference = adReference
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Notes",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        parsedNotes?.forEach() {
            if(it=="User Notes:"){
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp, 8.dp),) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            else if(it.startsWith("-")) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(0.dp, 8.dp),) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )

                }
            }
            else{
                Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                        .padding(0.dp, 8.dp),) {
                    Text(
                        text = "- $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(100.dp))
    }
}

@Composable
fun TitleCardImage(thisRecipe: SavedRecipe, imageHeight:Dp){
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }

    val currentFile = File(LocalContext.current.filesDir, thisRecipe.image)
    val filePath = currentFile.path
    val bitmap = BitmapFactory.decodeFile(filePath)
    val painter = remember{ BitmapPainter(image = bitmap.asImageBitmap())}

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = (sizeImage.height.toFloat()*0.66).toFloat(),  // 1/3
        endY = sizeImage.height.toFloat()
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(imageHeight)
        .onGloballyPositioned {
            sizeImage = it.size
        })
    {

        Image(
            painter = painter,
            contentDescription = thisRecipe.title,
            modifier = Modifier
                .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )


        Box(modifier = Modifier
            .matchParentSize()
            .background(gradient))
        Text(
            text = thisRecipe.title!!,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomStart),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TitleCardLoading(thisRecipe: SavedRecipe, imageHeight:Dp) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = (sizeImage.height.toFloat()*0.66).toFloat(),  // 1/3
        endY = sizeImage.height.toFloat()
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight())
    {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
            .onGloballyPositioned {
                sizeImage = it.size
            }
        )
        {
            Box(modifier = Modifier.align(Alignment.Center)){
                Indicator()
            }
        }
        Box(modifier = Modifier
            .matchParentSize()
            .background(gradient))
        Text(
            text = thisRecipe.title!!,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomStart),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TitleCardFull(thisRecipe: SavedRecipe, imageFlag:MutableState<Boolean>, imageFlag2:MutableState<Boolean>){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val TAG = "recipe Image Rewarded"

    val mRewardedAd:MutableState<RewardedAd?> = remember{ mutableStateOf(null) }
    var loadAttempted by remember{mutableStateOf(false)}
    if(!loadAttempted) {
        loadRewardedAd(context, mRewardedAd, TAG)
        loadAttempted=true
    }
    var imageCredits by remember { mutableStateOf(0) }

    var loadedFlag by remember { mutableStateOf(false) }

    if (!loadedFlag) {
        val loadedData = runBlocking { context.dataStore.data.first() }

        loadedData[savedPreferences]?.let {
            val retrievedData: SettingsObject = try {
                Json.decodeFromString<SettingsObject>(it)
            }catch(exception: Exception){
                SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
            }
            imageCredits = retrievedData.imageCredits

        }
        loadedFlag = true
    }

    val adFrameFlag = remember{ mutableStateOf(false) }


    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight())
    {
        if (imageFlag.value) {
            TitleCardLoading(thisRecipe, screenWidth)
        }
        if (imageFlag2.value){
            TitleCardImage(thisRecipe, screenWidth)
        }
        if (!imageFlag.value && !imageFlag2.value) {
            Column(
                modifier = Modifier
                .fillMaxSize()
            ) {
                Text(
                    text = thisRecipe.title!!,
                    modifier = Modifier
                        .padding(24.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                thisRecipe.imageDescription?.let{
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 5.dp)
                    ){
                        Text(text = "Image Generation:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 5.dp)
                    ) {
                        Text(
                            text = "Image Credits:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text =  imageCredits.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 5.dp),
                        onClick = {
                            if(imageCredits>0) {
                                val loadedData = runBlocking { context.dataStore.data.first() }
                                imageCredits-=1
                                loadedData[savedPreferences]?.let {
                                    val retrievedData: SettingsObject = try {
                                        Json.decodeFromString(it)
                                    }catch(exception: Exception){
                                        SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
                                    }
                                    retrievedData.imageCredits=imageCredits
                                    scope.launch {
                                        context.dataStore.edit { settings ->
                                            settings[savedPreferences] = Json.encodeToString(retrievedData)
                                        }
                                    }
                                }
                                imageFlag.value = true
                                getImage(it, context) {
                                    saveImage(context, thisRecipe, it.data[0].url) { it2 ->
                                        imageFlag2.value = it2
                                    }
                                }
                            }
                        }) {
                        Text(text = "Use Image Credit")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 5.dp),
                        onClick = {
                            adFrameFlag.value=true
                        }) {
                        Text(text = "Watch an ad")
                    }
                }
            }
        }
    }
    if(adFrameFlag.value)
        RewardedAdFrame(
            mRewardedAd = mRewardedAd,
            context = context,
            imageFlag = imageFlag,
            imageFlag2 = imageFlag2,
            thisRecipe = thisRecipe,
            displayFlag = adFrameFlag
        )
}

