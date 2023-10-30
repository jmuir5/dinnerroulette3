package com.noxapps.dinnerroulette3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchaseHistory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.noxapps.dinnerroulette3.billing.BillingWrapper
import com.noxapps.dinnerroulette3.billing.PurchasesPage
import com.noxapps.dinnerroulette3.home.HomePage
import com.noxapps.dinnerroulette3.ui.theme.*
import io.objectbox.Box
import io.objectbox.BoxStore
import kotlinx.coroutines.*
import kotlin.random.Random
import com.noxapps.dinnerroulette3.input.NewInput
import com.noxapps.dinnerroulette3.input.SettingsObject
import com.noxapps.dinnerroulette3.input.SpecificRecipeInput
import com.noxapps.dinnerroulette3.recipe.Recipe
import com.noxapps.dinnerroulette3.recipe.SavedRecipe
import com.noxapps.dinnerroulette3.recipe.SavedRecipe_
import com.noxapps.dinnerroulette3.search.SearchPage
import com.noxapps.dinnerroulette3.settings.RedeemCode
import com.noxapps.dinnerroulette3.settings.Settings
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPreset
import com.noxapps.dinnerroulette3.settings.dietpreset.DietPresetPage
import com.noxapps.dinnerroulette3.settings.dietpreset.initiliseDietPreset
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saveData")
val savedPreferences = stringPreferencesKey("savedPreferences")
val code1State = booleanPreferencesKey("code1State")
val usedTokens = intPreferencesKey("usedTokens")
val firstRun = booleanPreferencesKey("firstRun")
val imageCredits = intPreferencesKey("imageCredits")
val adFlag = booleanPreferencesKey("adFlag")


/**
 * main activity file
 */
class MainActivity : ComponentActivity() {


    companion object {
        private const val TAG = "MainActivity"
    }
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "init - before")
        val presetBox = ObjectBox.store.boxFor(DietPreset::class.java)
        if(presetBox.isEmpty) initiliseDietPreset(presetBox)
        val billingClient = BillingWrapper(this, CoroutineScope(Dispatchers.IO)).billingClient
        val scope = CoroutineScope(Dispatchers.IO)
        val context = this
        scope.launch {
            context.dataStore.edit { settings ->
                //Log.d("debug-credits add ",(credits+add).toString())
                settings[imageCredits] = 2
            }
        }


        ReminderNotificationWorker.schedule(this, 16, 0)
        /*val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val listner = PurchasesResponseListener(){result, purchases->
            Log.d("debug-billing purchases", result.toString())

            Log.d("debug-billing purchases size", purchases.size.toString())
            for(purchase in purchases){
                val consumeParams =
                    ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                val consumeResponseListener = ConsumeResponseListener(){billingResult, string->
                    Log.d("debug-billing consumeResponse1", billingResult.toString())
                    Log.d("debug-billing consumeResponse2", string)
                }

                billingClient.consumeAsync(consumeParams, consumeResponseListener)
                //Log.d("debug-billing consume", consumeResult.toString())
            }

        }
        billingClient.queryPurchasesAsync(params, listner)*/

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
                    val context = LocalContext.current
                    val loadedData = runBlocking { context.dataStore.data.first() }
                    Log.d("datastore", "checking")
                    if (loadedData[firstRun] == null) {
                        Log.d("datastore", "Init required")

                        initialiseDataStore(context, rememberCoroutineScope())
                    }
                    val navController = rememberNavController()
                    NavMain(navController, billingClient)

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
fun StandardScaffold(tabt:String, navController:NavHostController, adFlag:Boolean = true, homePageFlag:Boolean = false, content:@Composable () -> Unit){
    val scrollBehaviour = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text(
                        text = tabt,//topAppBarText.value,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehaviour,
                navigationIcon = {
                    if (homePageFlag){
                        IconButton(colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                            onClick = {
                                navController.navigate(Paths.Settings.Path)
                            }
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                    else {
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
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
                    val adReference = if(BuildConfig.DEBUG){
                        LocalContext.current.getString(R.string.test_scaffold_banner_ad_id)
                    }
                    else LocalContext.current.getString(R.string.scaffold_banner_ad_id)
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
    )
}


/**
 * composabe Drawer item for recipes
 */
@Composable
fun DrawerRecipeItem(input: SavedRecipe,
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
fun NavMain(navController: NavHostController, billingClient: BillingClient){
    val uri = "chefroulette://noximilionapplications.com"

    NavHost(navController = navController, startDestination = Paths.Home.Path) {
        composable(Paths.Home.Path) { HomePage(navController = navController) }
        composable(Paths.NewInput.Path) { NewInput(navController = navController) }
        composable(Paths.NatLanInput.Path) { NatLanInput(navController = navController) }
        composable(Paths.SpecificRecipeInput.Path) { SpecificRecipeInput(navController = navController) }
        composable(Paths.Settings.Path) { Settings(navController = navController) }
        composable(Paths.DietPreset.Path){ DietPresetPage(navController = navController) }
        composable(Paths.Search.Path) { SearchPage(navController = navController) }
        composable(Paths.Redeem.Path) { RedeemCode(navController = navController) }
        composable(Paths.Billing.Path){PurchasesPage(billingClient = billingClient, navController = navController)}
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
                ErrorPage(ErrorBody)
            }else{
                ErrorPage("An Unspecified Error Has Occurred")
            }


        }
        composable(
            route = "page/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink {
                uriPattern = "$uri/page/{id}"
                action= Intent.ACTION_VIEW
            })
        ) { backStackEntry ->
            val arguments = backStackEntry.arguments
            Log.d("notification info", "recieved")
            arguments?.getString("id")?.let { Log.d("notification info", it) }

            when(arguments?.getString("id")){
                "Home"-> HomePage(navController = navController)
                "NewInput" -> NewInput(navController = navController)
                "SpecificRecipeInput"-> SpecificRecipeInput(navController = navController)
                "Search"-> SearchPage(navController = navController)
                else -> HomePage(navController = navController)
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

fun initialiseDataStore(context:Context, scope:CoroutineScope){
    Log.d("datastore", "Init started")

    val defaultSettings = SettingsObject(false, false, listOf(), 0, 0, 0, 0, 2)
    scope.launch {
        context.dataStore.edit { settings ->
            settings[savedPreferences] = Json.encodeToString(defaultSettings)
            settings[code1State] = false
            settings[imageCredits] = 2
            settings[adFlag]=true
            settings[firstRun] = true
        }
        Log.d("datastore", "Init successfull")
    }
}