package com.noxapps.dinnerroulette3

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.noxapps.dinnerroulette3.ui.theme.*
import kotlinx.coroutines.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import io.objectbox.Box
import io.objectbox.BoxStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saveData")
val savedPreferences = stringPreferencesKey("savedPreferences")
val usedTokens = intPreferencesKey("usedTokens")

/**
 * main activity file
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ObjectBox.init(this)
        //normalising code
        /*val recipeBox: Box<SavedRecipe> = ObjectBox.store.boxFor(SavedRecipe::class.java)
        val allRecipes = recipeBox.all
        allRecipes.forEach(){
            it.title = it.title?.trim()
            it.description = it.description?.trim()
            it.ingredients = it.ingredients?.trim()
            it.method = it.method?.trim()
            it.notes = it.notes?.trim()
            recipeBox.put(it)
        }*/

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    //color = com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange//MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val tabtx = remember { mutableStateOf("dinner Roulette") }
                    NavMain(navController, tabtx)

                }
            }
        }
    }
}


/**
 * common drawer and scaffold for top bar used on the majority of pages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerAndScaffold(tabt:String, navController:NavHostController, content:@Composable () -> Unit){
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val items = recipeBox.all
    var topAppBarText = remember{ mutableStateOf(tabt)}

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                BackHandler(
                    enabled = drawerState.isOpen,
                ) {
                    scope.launch { drawerState.close() }
                }
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        navController.navigate(Paths.Home.Path){
                            popUpTo("Home" ){
                                inclusive = true
                            }
                        }
                        scope.launch { drawerState.close()}
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("New Recipie - Classic") },
                    selected = false,
                    onClick = {

                        navController.navigate(Paths.NewInput.Path){
                            popUpTo("Home")
                        }
                        scope.launch { drawerState.close()}

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("New Recipie - Request") },
                    selected = false,
                    onClick = {

                        navController.navigate(Paths.SpecificRecipeInput.Path){
                            popUpTo("Home")
                        }
                        scope.launch { drawerState.close()}

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Divider(
                    color = MaterialTheme.colorScheme.tertiary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(8.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Search") },
                    selected = false,
                    onClick = {

                        navController.navigate(Paths.Search.Path){
                            popUpTo("Home")
                        }
                        scope.launch { drawerState.close()}

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                LazyColumn(
                    modifier=Modifier.padding(horizontal=8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    items(items.size){item->
                        Spacer(Modifier.height(1.dp))
                        DrawerRecipeItem(input = items[item],  navController = navController, scope = scope, drawerState = drawerState)
                    }
                }
                Divider(
                    color = MaterialTheme.colorScheme.tertiary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        navController.navigate(Paths.Settings.Path) {
                            popUpTo("Home")
                        }
                        scope.launch { drawerState.close()}

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )



            }
        }
    ) {
        val scrollBehaviour = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            //modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = topAppBarText.value,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehaviour,
                    navigationIcon = {
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                if (drawerState.isClosed) scope.launch { drawerState.open() }
                                else scope.launch { drawerState.close() }
                            }
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            content = { padding ->

                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    content()
                }
            }
        )

    }
}


/**
 * composabe Drawer item for recipes
 */
@Composable
fun DrawerRecipeItem(input:SavedRecipe, navController: NavHostController, scope:CoroutineScope, drawerState:DrawerState){
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Article, contentDescription = null) },
        label = { Text(text = input.title!!) },
        selected = false,
        onClick = {
            navController.navigate(Paths.Recipe.Path+"/"+input.id){
                popUpTo("Home")
            }
            scope.launch { drawerState.close()}
        },
        modifier  = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )


}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        //DrawerAndScaffold()
    }
}

/**
 * nav controller
 */

@Composable
fun NavMain(navController: NavHostController, TABT:MutableState<String>){//, realm: Realm) {
    NavHost(navController = navController, startDestination = Paths.Home.Path) {
        composable(Paths.Home.Path) { HomePage(navController = navController) }
        composable(Paths.NewInput.Path) { NewInput(navController = navController) }
        composable(Paths.NatLanInput.Path) { NatLanInput(navController = navController) }
        composable(Paths.SpecificRecipeInput.Path) { SpecificRecipeInput(navController = navController) }
        composable(Paths.Settings.Path) { Settings(TABT = TABT) }
        composable(Paths.Search.Path) { SearchPage(navController = navController) }
        composable(Paths.Recipe.Path+"/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType })) {
            val recipeId = it.arguments?.getLong("recipeId")
            if (recipeId != null) {
                Recipe(recipeId, navController)
            }
            else{
                //todo: error code
            }
        }
        composable(Paths.Error.Path+"/{ErrorBody}",
            arguments = listOf(
                navArgument("ErrorBody") { type = NavType.StringType})) {
            val ErrorBody = it.arguments?.getString("ErrorBody")
            if (ErrorBody != null) {
                ErrorPage(ErrorBody, TABT = TABT)
            }else{
                ErrorPage("An Unspecified Error Has Occurred", TABT = TABT)
            }


        }
        /*...*/
    }
}

/**
 * object box helper function thing taken direcly from the object box tutorial
 */
object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }
}