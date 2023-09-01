package com.noxapps.dinnerroulette3

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.noxapps.dinnerroulette3.ui.theme.*
import io.objectbox.Box
import io.objectbox.BoxStore
import kotlinx.coroutines.*
import kotlin.random.Random
import com.noxapps.dinnerroulette3.AdMob


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

        MobileAds.initialize(this) { }
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
fun DrawerAndScaffold(tabt:String, navController:NavHostController, adFlag:Boolean = true, content:@Composable () -> Unit){
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val items = recipeBox.all
    var topAppBarText = remember{ mutableStateOf(tabt)}
    val recents = lastFive(recipeBox)
    val faves = faveFive(recipeBox)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                BackHandler(
                    enabled = drawerState.isOpen,
                ) {
                    scope.launch { drawerState.close() }
                }
                Column(modifier = Modifier
                    .weight(2f)) {
                    Spacer(Modifier.height(12.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        label = { Text("New Recipe") },
                        selected = false,
                        onClick = {
                            navController.navigate(Paths.Home.Path) {
                                popUpTo("Home") {
                                    inclusive = true
                                }
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    /*NavigationDrawerItem(
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
                    )*/
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text("Browse / Search") },
                        selected = false,
                        onClick = {
                            navController.navigate(Paths.Search.Path) {
                                popUpTo("Home")
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.tertiary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Text("Favourites",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(24.dp, 0.dp)
                )
                LazyColumn(
                    modifier= Modifier
                        .padding(horizontal = 8.dp)
                        .weight(4f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (faves.isEmpty()) {
                        item() {
                            Text("No favourite recipes. Make some recipes you love!")
                        }
                    } else {
                        item() {
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                label = { Text("Random Favourite Recipe") },
                                selected = false,
                                onClick = {
                                    navController.navigate(
                                        Paths.Recipe.Path + "/" + randomFavourite(
                                            recipeBox
                                        )
                                    ) {
                                        popUpTo("Home")
                                    }
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                        items(faves.size) { item ->
                            Spacer(Modifier.height(1.dp))
                            DrawerRecipeItem(
                                input = recipeBox[faves[item]],
                                navController = navController,
                                scope = scope,
                                drawerState = drawerState,
                                icon = Icons.Default.Favorite
                            )
                        }
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.tertiary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(8.dp)
                )
                Text("Recent",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(24.dp, 0.dp)
                )

                LazyColumn(
                    modifier= Modifier
                        .padding(horizontal = 8.dp)
                        .weight(4f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    if(recents.isEmpty()){
                        item(){
                            Text("No recent recipes. Make something new!")
                        }
                    }
                    else{
                        item(){
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Article, contentDescription = null) },
                                label = { Text("Random Previous Recipe") },
                                selected = false,
                                onClick = {
                                    navController.navigate(Paths.Recipe.Path+"/"+randomSaved(recipeBox)){
                                        popUpTo("Home")
                                    }
                                    scope.launch { drawerState.close()}
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                        items(recents.size){item->
                            Spacer(Modifier.height(1.dp))
                            DrawerRecipeItem(input = recipeBox[recents[item]],  navController = navController, scope = scope, drawerState = drawerState)
                        }
                    }

                }
                Column(modifier = Modifier
                    .weight(1f)) {
                    Divider(
                        color = MaterialTheme.colorScheme.tertiary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(8.dp)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("Settings") },
                        selected = false,
                        onClick = {
                            navController.navigate(Paths.Settings.Path) {
                                popUpTo("Home")
                            }
                            scope.launch { drawerState.close() }

                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
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
                Column (verticalArrangement = Arrangement.SpaceEvenly){
                    Box(
                        Modifier
                            .padding(padding)
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        content()
                    }
                    if(adFlag){
                        Row(){
                            Spacer(modifier = Modifier
                                .height(50.dp))
                            AdmobBanner(modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                            )
                        }
                    }

                }
            }
        )

    }
}


/**
 * composabe Drawer item for recipes
 */
@Composable
fun DrawerRecipeItem(input:SavedRecipe,
                     navController: NavHostController,
                     scope:CoroutineScope,
                     drawerState:DrawerState,
                     icon: ImageVector = Icons.Default.Article ){
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
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
        composable(Paths.Settings.Path) { Settings(navController = navController) }
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
fun faveFive(box:Box<SavedRecipe>):List<Long>{
    val query = box.query(SavedRecipe_.favourite.equal(true)).build()
    val orderedFaves = query.findIds()
    val randomFaves = mutableListOf<Long>()
    if(orderedFaves.isNotEmpty()) {
        while (randomFaves.size < 5) {
            val randInt = Random.nextInt(orderedFaves.size)
            if (!randomFaves.contains(orderedFaves[randInt])) {
                randomFaves.add(orderedFaves[randInt])
            }
            if (randomFaves.size == orderedFaves.size) break
        }
    }
    return randomFaves
}

/**
 * generates a list of the last 5 generated recipes
 */
fun lastFive(box:Box<SavedRecipe>):List<Long>{
    val allRecipes = box.all
    val lastFive:MutableList<Long> = mutableListOf()
    if(allRecipes.size>=5) {
        for (i in allRecipes.size downTo (allRecipes.size-4)) {
            lastFive.add(box[(i).toLong()].id)
        }
    }
    else for(i in allRecipes.size downTo 1){
        lastFive.add(box[i.toLong()].id)
    }
    return lastFive
}

/**
 * returns a random saved recipe
 */
fun randomSaved(box:Box<SavedRecipe>):Long{
    val allRecipes = box.all
    return allRecipes[Random.nextInt(allRecipes.size)].id
}

/**
 * returns a random favourited saved recipe
 */
fun randomFavourite(box:Box<SavedRecipe>):Long{
    val query = box.query(SavedRecipe_.favourite.equal(true)).build()
    val allFaves = query.findIds()
    return allFaves[Random.nextInt(allFaves.size)]
}