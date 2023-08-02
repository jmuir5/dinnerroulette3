package com.noxapps.dinnerroulette3

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
import io.objectbox.BoxStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saveData")
val savedPreferences = stringPreferencesKey("savedPreferences")
val usedTokens = intPreferencesKey("usedTokens")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Realm.Init(Context)
        /*lifecycleScope.launch {
            context.dataStore.data.first()
            // You should also handle IOExceptions here.
        }*/
        ObjectBox.init(this)

        setContent {
            DinnerRoulette3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange//MaterialTheme.colorScheme.background
                ) {
                    MainScaffold()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScaffold(){
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dinner Roulette") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (drawerState.isClosed) scope.launch { drawerState.open()}
                            else scope.launch { drawerState.close()}
                        /* "Open nav drawer" */ }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryOrange,
                    titleContentColor = Black)
            )
        },
        content = { padding ->

            Box(
                Modifier.padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceOrange),  // BG image behind AppBar
                ) {
                    Drawer(drawerState, scope)
                //OptionsInput()
                    /*Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )*/
                }

            }
        }
    )
}
/*var holder = SavedRecipe(QandA(
    Query("Optional", "Any", "Any", "(Optional)", mutableListOf<String>(), mutableListOf<String>(), mutableListOf<String>()),
    GptResponse("default", "default response", 0,"default", listOf(
        GptChoices(0,
            GptMessage("0", "0"),"finish")
    ),
        GptUsage(1, 1, 2) ),
    ParsedResponse("1","2", "3", "4", "5")))
*/

@Composable
fun Drawer(drawerState: DrawerState, scope:CoroutineScope){
// icons to mimic drawer destinations
    val navController = rememberNavController()
    val recipeBox = ObjectBox.store.boxFor(SavedRecipe::class.java)
    val items: List<SavedRecipe> = recipeBox.all


    //val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceOrange,
                drawerContentColor = SurfaceOrange
                ) {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("New Recipie") },
                    selected = false,
                    onClick = {
                        navController.navigate(Paths.NewInput.Path)
                        scope.launch { drawerState.close()}

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        navController.navigate(Paths.Settings.Path)
                        scope.launch { drawerState.close()}

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(12.dp))

                items.forEachIndexed() { index, item ->
                    DrawerRecipeItem(input = item, index = index,  navController = navController, scope = scope, drawerState = drawerState)
                }

                Spacer(Modifier.height(12.dp))



            }
        },
        content = {
            NavMain(navController)


        }
    )
}


@Composable
fun DrawerRecipeItem(input:SavedRecipe, index:Int, navController: NavHostController, scope:CoroutineScope, drawerState:DrawerState){
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Article, contentDescription = null) },
        label = { Text(text = input.title!!) },
        selected = false,
        onClick = {
            navController.navigate(Paths.Recipe.Path+"/"+input.id)
            scope.launch { drawerState.close()}
            //contentLocation="Recipe"

            //open recipe
        },
        modifier  = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )


}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DinnerRoulette3Theme {
        MainScaffold()
    }
}

@Composable
fun NavMain(navController: NavHostController){//, realm: Realm) {
    NavHost(navController = navController, startDestination = Paths.NewInput.Path) {
        composable(Paths.NewInput.Path) {
            NewInput(navController = navController)//, realm = realm)
        }
        composable(Paths.Settings.Path) { Settings() }
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
        /*...*/
    }
}

object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }
}