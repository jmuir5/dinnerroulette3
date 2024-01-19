package com.noxapps.dinnerroulette3.recipe

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.ads.rewarded.RewardedAd
import com.noxapps.dinnerroulette3.AdmobBanner
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.InterstitialAdDialogue
import com.noxapps.dinnerroulette3.commons.Indicator
import com.noxapps.dinnerroulette3.ObjectBox
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.RewardedAdFrame
import com.noxapps.dinnerroulette3.commons.AdOrShopDialogue
import com.noxapps.dinnerroulette3.commons.FavouriteButton
import com.noxapps.dinnerroulette3.commons.ProcessingDialog
import com.noxapps.dinnerroulette3.commons.StyledLazyRow
import com.noxapps.dinnerroulette3.commons.addImageCredits
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.commons.getImageCredits
import com.noxapps.dinnerroulette3.gpt.getImage
import com.noxapps.dinnerroulette3.gpt.saveImage
import com.noxapps.dinnerroulette3.input.Query
import com.noxapps.dinnerroulette3.loadInterstitialAd
import com.noxapps.dinnerroulette3.loadRewardedAd
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun Recipe(
    recipeId:Long,
    navController: NavHostController
) {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    var thisRecipe by remember {mutableStateOf(recipeBox[recipeId])}

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

    val regenDialogueState = remember{ mutableStateOf(false) }
    val shareDialogueState = remember{ mutableStateOf(false) }
    val deleteDialogueState = remember{ mutableStateOf(false) }
    val deleteActionState = remember{ mutableStateOf(false) }
    val processingDialogueState = remember{ mutableStateOf(false) }

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
                            text = thisRecipe.title?:"",
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
                    actions = {
                        OverflowActionButtons(
                            regenDialogueState = regenDialogueState,
                            shareDialogueState = shareDialogueState,
                            deleteDialogueState = deleteDialogueState,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = iconButtonBackgroundColor.value,
                                contentColor = iconColor.value
                            )
                        )
                    },
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = thisRecipe.title?:"undefined",
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    actions = {
                        OverflowActionButtons(
                            regenDialogueState = regenDialogueState,
                            shareDialogueState = shareDialogueState,
                            deleteDialogueState = deleteDialogueState,
                        )
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
            TitleCardFull(thisRecipe = thisRecipe, imageFlag = imageFlag, imageFlag2 = imageFlag2, navController = navController)
            RecipeBody(thisRecipe = thisRecipe)
            if(regenDialogueState.value){
                RegenerateDialogue(
                    state = regenDialogueState,
                    processingState = processingDialogueState,
                    recipe = thisRecipe,
                    navController = navController
                )
            }
            if(shareDialogueState.value){

            }
            if(deleteDialogueState.value){
                DeleteDialogue(state = deleteDialogueState, actionState = deleteActionState)
            }
            if(deleteActionState.value){
                deleteActionState.value=!deleteActionState.value
                thisRecipe = SavedRecipe()
                recipeBox.remove(recipeId)
                navController.popBackStack()
            }
            if(processingDialogueState.value){
                ProcessingDialog(text = "Regenerating your recipe for ${thisRecipe.title}")
            }

        }
    }
}

@Composable
fun RecipeBody(
    thisRecipe: SavedRecipe
) {
    val parsedIngredients = thisRecipe.ingredients?.split("\n")
    val parsedMethod = thisRecipe.method?.split("\n")
    val parsedNotes = thisRecipe.notes?.split("\n")

    val context = LocalContext.current
    val adFlag = getAdFlag(context)
    val adReference = if(BuildConfig.DEBUG){
        context.getString(R.string.test_scaffold_banner_ad_id)
    }
    else context.getString(R.string.scaffold_banner_ad_id)
    Column(
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = thisRecipe.description?:"",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(10.dp))
        if (adFlag) {
            Row() {
                Spacer(
                    modifier = Modifier
                        .height(50.dp)
                )
                AdmobBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    reference = adReference
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        parsedIngredients?.forEach() {
            if (it.isEmpty()) {
                Spacer(modifier = Modifier.size(10.dp))
            } else if (it.startsWith("-")) {
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
            } else {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        if (adFlag) {
            Row() {
                Spacer(
                    modifier = Modifier
                        .height(50.dp)
                )

                AdmobBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    reference = adReference
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Method",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        parsedMethod?.forEach() {
            if(it.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(0.dp, 8.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {
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
        }
        Spacer(modifier = Modifier.size(10.dp))
        if (adFlag) {
            Row() {
                Spacer(
                    modifier = Modifier
                        .height(50.dp)
                )

                AdmobBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    reference = adReference
                )
            }
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
            text = thisRecipe.title?:"",
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
            text = thisRecipe.title?:"",
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomStart),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TitleCardFull(
    thisRecipe: SavedRecipe,
    imageFlag:MutableState<Boolean>,
    imageFlag2:MutableState<Boolean>,
    navController: NavHostController
){
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
    var imageCredits by remember { mutableStateOf(getImageCredits(context)) }

    val shopPrompt = remember{ mutableStateOf(false) }

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
                    text = thisRecipe.title?:"",
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
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 5.dp)
                    ) {

                        Button(
                            modifier = Modifier
                                .padding(5.dp, 5.dp)
                                .weight(5F),
                            onClick = {
                                adFrameFlag.value = true
                            }) {
                            Text(text = "Watch an ad")
                        }
                        Button(
                            modifier = Modifier
                                .padding(5.dp, 5.dp)
                                .weight(5F),
                            onClick = {
                                if (imageCredits > 0) {
                                    addImageCredits(context, -1)
                                    imageFlag.value = true
                                    try {
                                        getImage(it, context) {
                                            saveImage(context, thisRecipe, it.data[0].url) { it2 ->
                                                imageFlag2.value = it2
                                            }
                                        }
                                    }
                                    catch (e:Exception){
                                        addImageCredits(context, 1)
                                        navController.navigate(Paths.Error.Path+"/${e}")
                                    }
                                }else shopPrompt.value=true

                            }) {
                            Text(text = "Use Image Credit")
                        }
                    }
                }
            }
        }
    }
    if(adFrameFlag.value) {
        RewardedAdFrame(
            mRewardedAd = mRewardedAd,
            context = context,
            imageFlag = imageFlag,
            imageFlag2 = imageFlag2,
            thisRecipe = thisRecipe,
            displayFlag = adFrameFlag
        )
    }
    if(shopPrompt.value){
        AdOrShopDialogue(thisState = shopPrompt, adState = adFrameFlag, navController = navController)
    }
}

@Composable
fun OverflowActionButtons(
    regenDialogueState:MutableState<Boolean>,
    shareDialogueState:MutableState<Boolean>,
    deleteDialogueState:MutableState<Boolean>,
    colors: IconButtonColors? = null
){
    var overflowDD by remember { mutableStateOf(false) }
    var overflowIcon by remember{mutableStateOf(Icons.Filled.MoreHoriz)}
    overflowIcon = if (overflowDD)
        Icons.Filled.MoreHoriz
    else
        Icons.Filled.MoreVert

    IconButton(
        colors = colors
            ?: IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        onClick = {
            overflowDD = !overflowDD
        }
    ) {
        Icon(overflowIcon, contentDescription = "Overflow")
    }
    DropdownMenu(
        expanded = overflowDD,
        onDismissRequest = { overflowDD = false },
        modifier = Modifier
        //.fillMaxWidth()
        //.padding(24.dp, 4.dp),

    ) {
        DropdownMenuItem(
            onClick = {
                overflowDD = false
                regenDialogueState.value = true
            },
            text = {
                Row(){
                    Icon(Icons.Filled.Refresh, contentDescription = "Regenerate")
                    Text(text = "Regenerate")
                }
            }
        )
        /*
        DropdownMenuItem(
            onClick = {//todo share
                overflowDD = false
                shareDialogueState.value = true
            },
            text = {
                Row(){
                    Icon(Icons.Filled.Share, contentDescription = "Share")
                    Text(text = "Share")
                }
            }
        )
         */
        DropdownMenuItem(
            onClick = {
                overflowDD = false
                deleteDialogueState.value = true
            },
            text = {
                Row(){
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    Text(text = "Delete")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegenerateDialogue(
    state: MutableState<Boolean>,
    processingState: MutableState<Boolean>,
    viewModel: RegenerateViewModel = RegenerateViewModel(),
    recipe:SavedRecipe,
    navController: NavHostController
) {
    val focusRequester = remember { FocusRequester() }

    var similarityDD by remember { mutableStateOf(false) }
    var similarityIndex by remember { mutableIntStateOf(0) }

    var text by remember { mutableStateOf("") }
    val modifierArray = remember{mutableStateListOf<String>()}
    val queryState = derivedStateOf { Query() != Query(recipe) }
    Log.d("debug query none", Query().toString())
    Log.d("debug query constructed", Query(recipe).toString())

    var modifierState by remember{mutableStateOf(false)}

    var replaceState by remember{mutableStateOf(true)}

    val context = LocalContext.current

    val titleAdFrameFlag = remember { mutableStateOf(false) }
    val queryAdFrameFlag = remember { mutableStateOf(false) }
    val fullAdFrameFlag = remember { mutableStateOf(false) }
    val adReference = if(BuildConfig.DEBUG){
        LocalContext.current.getString(R.string.test_regenerate_interstitial_ad_id)
    }
    else LocalContext.current.getString(R.string.regenerate_interstitial_ad_id)

    loadInterstitialAd(context, viewModel.mInterstitialAd, viewModel.TAG, adReference)

    BasicAlertDialog(
        onDismissRequest = {
            state.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Regenerate Recipe",
                    style = MaterialTheme.typography.headlineMedium,
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .clickable(onClick = {
                            similarityDD = true
                        })
                        .padding(0.dp, 8.dp)
                ) {
                    Text(
                        text = "Regenerate from:",
                        style = MaterialTheme.typography.titleMedium

                    )
                    DropdownMenu(
                        expanded = similarityDD,
                        onDismissRequest = { similarityDD = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 4.dp),

                        ) {
                        viewModel.similarityItems.forEachIndexed() { index, s ->
                            if(index!=1||queryState.value) {
                                DropdownMenuItem(
                                    onClick = {
                                        similarityIndex = index
                                        similarityDD = false
                                    }, text = {
                                        Text(
                                            text = s,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(0.dp, 4.dp)
                                        .fillMaxWidth()
                                )
                            }
                            
                        }
                    }
                    Text(
                        text = viewModel.similarityItems[similarityIndex],
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Row(                                                               //sav as new/replace
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp)
                ) {
                    Text(
                        text = "Replace this recipe?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Checkbox(checked = replaceState, onCheckedChange = {replaceState = it})

                }
                if(modifierState) {

                    StyledLazyRow(array = modifierArray, true)
                    Row() {
                        val maxChar = 17
                        TextField(
                            modifier = Modifier
                                .focusRequester(focusRequester),
                            value = text,
                            onValueChange = {
                                if (it.length <= maxChar) text = it
                            },
                            label = { Text("Modifiers") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (text.isNotEmpty()) {
                                        modifierArray.add(text)
                                        text = ""
                                    }
                                }
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = {
                            if (text.isNotEmpty()) {
                                modifierArray.add(text)
                                text = ""
                            }
                        }) {
                            Text(text = "Add")
                        }
                    }
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
                else{
                    Button(
                        modifier = Modifier,
                        onClick = { modifierState = !modifierState }) {
                        Text(text = "Add Modifiers...")
                    }
                }
                
                Button(
                    onClick = {
                        when(similarityIndex){
                            0-> {
                                titleAdFrameFlag.value = true
                            }
                            1->{
                                queryAdFrameFlag.value = true
                            }
                            2->{
                                fullAdFrameFlag.value = true
                            }
                        }
                    }
                ) {
                    Text(text = "Regenerate")
                }
            }
        }

    }
    if(titleAdFrameFlag.value){
        InterstitialAdDialogue(
            mInterstitialAd = viewModel.mInterstitialAd,
            context = context,
            displayFlag = fullAdFrameFlag,
            function = {
                titleAdFrameFlag.value=false
                viewModel.regenerateName(
                    promptText = recipe.title?:"undefined",
                    modifiers = modifierArray,
                    processingDialogueFlag = processingState,
                    saveID = if(replaceState) recipe.id else 0L,
                    presetId = viewModel.getPreset(context),
                    context = context,
                    navController = navController,
                )
                state.value = false
            }
        )
    }
    if(queryAdFrameFlag.value){
        InterstitialAdDialogue(
            mInterstitialAd = viewModel.mInterstitialAd,
            context = context,
            displayFlag = fullAdFrameFlag,
            function = {
                queryAdFrameFlag.value = false
                viewModel.regenerateQuery(
                    query = Query(recipe),
                    modifiers = modifierArray,
                    processingStateFlag = processingState,
                    saveID = if(replaceState) recipe.id else 0L,
                    context = context,
                    navController = navController,
                )
                state.value = false
            }
        )
    }
    if(fullAdFrameFlag.value){
        InterstitialAdDialogue(
            mInterstitialAd = viewModel.mInterstitialAd,
            context = context,
            displayFlag = fullAdFrameFlag,
            function = {
                fullAdFrameFlag.value=false
                viewModel.regenerateFull(
                    recipe = recipe,
                    modifiers = modifierArray,
                    processingStateFlag = processingState,
                    saveID = if(replaceState) recipe.id else 0L,
                    context = context,
                    navController = navController,
                )
                state.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDialogue(title:String?, body:String, state: MutableState<Boolean>) {
    BasicAlertDialog(onDismissRequest = {
        state.value=false
    }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                title?.let() {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            it,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        body,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Row() {
                    Button(onClick = {
                        state.value = false
                    }) {
                        Text(text = "OK")
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialogue(state: MutableState<Boolean>, actionState: MutableState<Boolean>) {
    val title = "Delete Recipe?"
    val body = "Are you sure you want to delete this recipe? \n There is no way to undo this."

    BasicAlertDialog(onDismissRequest = {
        state.value=false
    }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                title?.let() {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            it,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        body,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Row() {
                    Button(onClick = {
                        state.value = false
                    }) {
                        Text(text = "Cancel")
                    }
                    Button(onClick = {
                        state.value = false
                        actionState.value = true
                    }) {
                        Text(text = "Confirm")
                    }
                }
            }

        }
    }
}


