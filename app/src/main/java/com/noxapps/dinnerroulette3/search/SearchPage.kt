package com.noxapps.dinnerroulette3.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.AdmobBanner
import com.noxapps.dinnerroulette3.BuildConfig
import com.noxapps.dinnerroulette3.StandardScaffold
import com.noxapps.dinnerroulette3.commons.FreeFavouriteButton
import com.noxapps.dinnerroulette3.Paths
import com.noxapps.dinnerroulette3.R
import com.noxapps.dinnerroulette3.commons.getAdFlag
import com.noxapps.dinnerroulette3.recipe.SavedRecipe

@Composable
fun SearchPage(
    navController: NavHostController,
    viewModel: SearchViewModel = SearchViewModel()
) {
    val recipeStateList = remember{mutableStateListOf<SavedRecipe>()}
    val searchList = remember{mutableStateOf(viewModel.allRecipes.reversed())}
    viewModel.screenWidth = LocalConfiguration.current.screenWidthDp
    if(searchList.value!= recipeStateList){
        recipeStateList.clear()
        searchList.value.forEach{
            recipeStateList.add(it)
        }
    }



    StandardScaffold(tabt = "View Recipes", navController = navController, adFlag = false) {
        Column {
            SearchBlock(searchList, viewModel)
            RecipeList(recipeStateList, navController, viewModel)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBlock(recipesList:MutableState<List<SavedRecipe>>, viewModel: SearchViewModel) {
    /**
     * todo:
     * filters:
     * - exclude text?
     *
     * add tiles/row
     */
    val primaryOrange = MaterialTheme.colorScheme.primary
    var filterText by remember { mutableStateOf("")}
    var filterBool by remember { mutableStateOf(false) }
    val borderColor = if (filterBool)MaterialTheme.colorScheme.primary else Color.Transparent
    val filterBarBgColor =
        if(filterBool) Color.White
        else Color.LightGray

    var addInfoState by remember{mutableStateOf(false)}
    val addInfoIcon =
        if(addInfoState) Icons.Default.KeyboardArrowUp
        else Icons.Default.KeyboardArrowDown

    val searchLoc = listOf("All", "Title", "Ingredients", "Cuisine", "Description", "Method", "Notes" )
    var searchLocIndex by remember { mutableStateOf(0) }
    var searchLocState by remember { mutableStateOf(false) }

    val sortList = listOf("Date ↓", "Date ↑", "Title ↓", "Title ↑")
    var sortIndex by remember { mutableStateOf(0) }
    var sortState by remember { mutableStateOf(false) }

    val filterButtonChoices = listOf("Favourites", "Vegetarian", "Vegan", "Gluten Free")
    val filterButtonBools = mutableListOf<MutableState<Boolean>>()
    filterButtonChoices.forEach{ _->
        filterButtonBools.add(remember{mutableStateOf(false)})
    }
    val filterButtonIcons = listOf(
        listOf(Icons.Filled.FavoriteBorder,Icons.Filled.Favorite),
        listOf(Icons.Outlined.Eco,Icons.Filled.Eco),
        listOf(Icons.Outlined.Eco,Icons.Filled.Eco),
        listOf(Icons.Filled.FavoriteBorder,Icons.Filled.Favorite)
    )
    val filterBarColors = SearchBarDefaults.inputFieldColors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedLeadingIconColor = Color.Black,
        unfocusedLeadingIconColor = Color.Gray,
        focusedPlaceholderColor = Color.LightGray,
        unfocusedPlaceholderColor = Color.Gray,
    )
    var titButtonColors = CusTitButtonColors(
        ButtonBgColorInit = MaterialTheme.colorScheme.primaryContainer,
        ButtonBgColorChecked = MaterialTheme.colorScheme.primary,
        TextColorInit = MaterialTheme.colorScheme.onPrimaryContainer,
        TextColorChecked = MaterialTheme.colorScheme.onPrimary,
        IconColorInit = MaterialTheme.colorScheme.onPrimaryContainer,
        IconColorChecked = MaterialTheme.colorScheme.onPrimary
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .drawBehind {
            val borderSize = 4.dp.toPx()
            drawLine(
                color = primaryOrange,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = borderSize
            )
        }
    ){
        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp, 4.dp, 12.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .weight(8f)
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(100.dp)
                        )
                ) {
                    StaticSearchBar(
                        modifier = Modifier
                            .background(filterBarBgColor),
                        value = filterText,
                        onValueChange = { filterText = it
                                        if(filterText.isEmpty()){
                                            recipesList.value = viewModel.permuteList(
                                                viewModel.recipeBox,
                                                searchLocIndex,
                                                filterText,
                                                sortIndex,
                                                filterButtonBools)
                                        }},
                        onSearch = {recipesList.value = viewModel.permuteList(
                            viewModel.recipeBox,
                            searchLocIndex,
                            filterText,
                            sortIndex,
                            filterButtonBools)
                                   },
                        active = filterBool,
                        onActiveChange = { filterBool = it },
                        placeholder = { Text("Filter") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.FilterAlt,
                                contentDescription = "Filter"
                            )
                        },
                        colors = filterBarColors
                    )
                }
                IconButton(onClick = { addInfoState = !addInfoState }) {
                    Icon(addInfoIcon, contentDescription = "Advanced Search")
                }

            }
            if (addInfoState){
                Row(){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(5f),
                        contentAlignment = Alignment.TopEnd//???????????????????????????todo fix


                    ) {
                        Row(modifier = Modifier
                            .clickable { searchLocState = !searchLocState }
                            .fillMaxWidth()
                            .padding(12.dp, 4.dp, 12.dp, 0.dp)
                            .drawBehind {
                                val borderSize = 4.dp.toPx()
                                drawLine(
                                    color = primaryOrange,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = borderSize
                                )
                            },
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Search in:")
                            Text(searchLoc[searchLocIndex])
                        }
                        DropdownMenu(
                            expanded = searchLocState,
                            onDismissRequest = { searchLocState = false },
                            modifier = Modifier
                        ) {
                            searchLoc.forEachIndexed() { index, s ->
                                DropdownMenuItem(onClick = {
                                    searchLocIndex = index
                                    searchLocState = false
                                    recipesList.value = viewModel.permuteList(
                                        viewModel.recipeBox,
                                        searchLocIndex,
                                        filterText,
                                        sortIndex,
                                        filterButtonBools)

                                }, text = { Text(text = s, textAlign = TextAlign.End) },
                                    modifier = Modifier
                                        //.padding(8.dp)
                                )
                            }
                        }
                    }
                    Box (modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 4.dp, 12.dp, 0.dp)
                        .weight(5f),
                        contentAlignment = Alignment.BottomEnd//???????????????????????????todo fix
                    ){
                        Row(modifier = Modifier
                            .clickable { sortState = !sortState }
                            .fillMaxWidth()

                            .drawBehind {
                                val borderSize = 4.dp.toPx()
                                drawLine(
                                    color = primaryOrange,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = borderSize
                                )
                            },
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Sort by:")
                            Text(sortList[sortIndex])
                        }
                        DropdownMenu(
                            expanded = sortState,
                            onDismissRequest = { sortState = false },
                            //modifier = Modifier.fillMaxWidth()
                        ) {
                            sortList.forEachIndexed() { index, s ->
                                DropdownMenuItem(onClick = {
                                    sortIndex = index
                                    sortState = false
                                    recipesList.value = viewModel.permuteList(
                                        viewModel.recipeBox,
                                        searchLocIndex,
                                        filterText,
                                        sortIndex,
                                        filterButtonBools)
                                }, text = { Text(text = s, textAlign = TextAlign.End) },
                                    modifier = Modifier
                                        //.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
            LazyRow(modifier= Modifier.padding(0.dp, 4.dp, 0.dp, 4.dp),
                content = {
                    item(){
                        Spacer(modifier = Modifier.size(12.dp))
                    }
                    items(filterButtonChoices.size){
                        Row {
                            CusTitButton(
                                text =filterButtonChoices[it],
                                value = filterButtonBools[it].value,
                                onClick = {
                                    filterButtonBools[it].value = !filterButtonBools[it].value
                                    recipesList.value = viewModel.permuteList(
                                        viewModel.recipeBox,
                                        searchLocIndex,
                                        filterText,
                                        sortIndex,
                                        filterButtonBools)
                                          },
                                iconInit = filterButtonIcons[it][0],//Icons.Filled.FavoriteBorder,
                                iconChecked = filterButtonIcons[it][1] ,
                                colors = titButtonColors
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                        }
                    }
                    item(){
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            )
            //Text("this\nis\nwhere\nsearch\nparamaters\ngo")
        }

    }
}

@Composable
fun RecipeList(recipesList:List<SavedRecipe>, navController: NavHostController, viewModel: SearchViewModel){
    var counter = 0
    val adReference = if(BuildConfig.DEBUG){
        LocalContext.current.getString(R.string.test_scaffold_banner_ad_id)
    }
    else LocalContext.current.getString(R.string.scaffold_banner_ad_id)
    val adFlag = getAdFlag(LocalContext.current)
    LazyColumn{
        for (i in recipesList.indices step(3)){
            counter+=1
            item(){
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ){
                    RecipeCard(recipe = recipesList[i], navController = navController, viewModel)
                    if(i+1<recipesList.size){
                        RecipeCard(recipe = recipesList[i+1], navController = navController, viewModel)
                    }
                    else Spacer(modifier = Modifier
                        .width((viewModel.screenWidth / viewModel.tilesPerRow).dp))
                    if(i+2<recipesList.size){
                        RecipeCard(recipe = recipesList[i+2], navController = navController, viewModel)
                    }
                    else Spacer(modifier = Modifier
                        .width((viewModel.screenWidth / viewModel.tilesPerRow).dp))
                }
            }
            if(counter%2==0&&adFlag){
                item(){
                    Row(){
                        Spacer(modifier = Modifier
                            .height(50.dp))
                        AdmobBanner(modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                            reference = adReference
                        )
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
            .width((viewModel.screenWidth / viewModel.tilesPerRow).dp)
            .clickable { navController.navigate(Paths.Recipe.Path + "/" + recipe.id) }
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary)

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
            FreeFavouriteButton(id = recipe.id, modifier = Modifier.align(Alignment.TopEnd))
        }
        
        Row(modifier = Modifier
            .padding(2.dp)){
            Text(
                text = recipe.title!!,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                minLines = 3,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,

            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticSearchBar(value: String,
                    onValueChange: (String) -> Unit,
                    onSearch: (String) -> Unit,
                    active: Boolean,
                    onActiveChange: (Boolean) -> Unit,
                    modifier: Modifier = Modifier,
                    enabled: Boolean = true,
                    placeholder: @Composable (() -> Unit)? = null,
                    leadingIcon: @Composable (() -> Unit)? = null,
                    trailingIcon: @Composable (() -> Unit)? = null,
                    colors: TextFieldColors = SearchBarDefaults.inputFieldColors(),
                    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
){
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val textColor = LocalTextStyle.current.color.takeOrElse {
        MaterialTheme.colorScheme.onBackground
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(56.0.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onActiveChange(true) },
        enabled = enabled,
        singleLine = true,
        textStyle = LocalTextStyle.current.merge(TextStyle(color = textColor)),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = placeholder,
                leadingIcon = leadingIcon?.let { leading -> {
                    Box(Modifier.offset(x = 4.dp)) { leading() }
                } },
                trailingIcon = trailingIcon?.let { trailing -> {
                    Box(Modifier.offset(x = (-4).dp)) { trailing() }
                } },
                shape = SearchBarDefaults.inputFieldShape,
                colors = colors,
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(),
                container = {},
            )
        }
    )
    LaunchedEffect(active) {
        if (!active) {
            focusManager.clearFocus()
        }
    }
    BackHandler(enabled = active) {
        onActiveChange(false)
    }
}

/**
 * custom text icon toggle button,
 * i didnt see any thing quite like it so i made my own
 *
 */
@Composable
fun CusTitButton(modifier:Modifier = Modifier,
                 text:String,
                 value:Boolean,
                 onClick:()->Unit,
                 iconInit: ImageVector,
                 iconChecked:ImageVector,
                 colors: CusTitButtonColors = CusTitButtonColors(
                    ButtonBgColorInit = MaterialTheme.colorScheme.primaryContainer,
                    ButtonBgColorChecked = MaterialTheme.colorScheme.primary,
                    TextColorInit = MaterialTheme.colorScheme.onPrimaryContainer,
                    TextColorChecked = MaterialTheme.colorScheme.onPrimary,
                    IconColorInit = MaterialTheme.colorScheme.onPrimaryContainer,
                    IconColorChecked = MaterialTheme.colorScheme.onPrimary
                 )
){
    val backgroundColor =
        if(value) colors.ButtonBgColorChecked
        else colors.ButtonBgColorInit
    val textColor =
        if(value) colors.TextColorChecked
        else colors.TextColorInit
    val iconColor =
        if(value) colors.IconColorChecked
        else colors.IconColorInit
    val currentIcon =
        if(value) iconChecked
        else iconInit
    Button(modifier = Modifier
        ,
        colors = ButtonDefaults.textButtonColors(
            containerColor = backgroundColor
        ),
        onClick = onClick)
    {
        Text(text, color = textColor)
        Icon(currentIcon, contentDescription = text, tint = iconColor)
    }
}

class CusTitButtonColors(
    val ButtonBgColorInit:Color,
    val ButtonBgColorChecked:Color,
    val TextColorInit:Color,
    val TextColorChecked:Color,
    val IconColorInit:Color,
    val IconColorChecked:Color,
)
