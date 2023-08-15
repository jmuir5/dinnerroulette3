package com.noxapps.dinnerroulette3

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SearchPage(
    navController: NavHostController,
    viewModel: SearchViewModel = SearchViewModel()
) {
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val allRecipes by remember{ mutableStateOf(recipeBox.all)}
    viewModel.screenWidth = LocalConfiguration.current.screenWidthDp



    DrawerAndScaffold(tabt = "View Recipes", navController = navController) {
        Column {
            SearchBlock(allRecipes, viewModel)
            RecipeList(allRecipes, navController, viewModel)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBlock(recipesList:List<SavedRecipe>,  viewModel: SearchViewModel) {
    val primaryOrange = MaterialTheme.colorScheme.primary
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
        Column(){
            /**
             * todo:
             * add search bar (rip from searchBar())
             *
             * add sort by:
             * - name (ascending/descending)
             * - age (ascending decending)
             *
             * filters:
             * - text in name,
             * - text in ingredients,
             * - exclude text
             * - favourite status
             * - vegetarian/vegan status?
             *
             * add tiles/row
             */
            var filterText by remember { mutableStateOf("")}
            var filterBool by remember { mutableStateOf(false) }
            val borderColor = if (filterBool)MaterialTheme.colorScheme.primary else Color.Transparent
            val filterBarBgColor = if(filterBool) {
                    Color.White
                }
                else{
                    Color.LightGray
                }

            val filterBarColors = SearchBarDefaults.inputFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedLeadingIconColor = Color.Black,
                unfocusedLeadingIconColor = Color.Gray,
                focusedPlaceholderColor = Color.LightGray,
                unfocusedPlaceholderColor = Color.Gray,
            )
            Box(modifier = Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(100.dp))
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
                    onValueChange = { filterText = it },
                    onSearch = {},
                    active = filterBool,
                    onActiveChange = { filterBool = it },
                    placeholder = { Text("Filter") },
                    leadingIcon = { Icon(Icons.Default.FilterAlt, contentDescription = "Filter") },
                    colors = filterBarColors
                )
            }

            //SearchBar(query = , onQueryChange = , onSearch = , active = , onActiveChange = )


            Text("this\nis\nwhere\nsearch\nparamaters\ngo")
        }
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