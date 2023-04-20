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
import com.noxapps.dinnerroulette3.ui.theme.*
import kotlinx.coroutines.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.where
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
var holder = SavedRecipe(QandA(
    Query("Optional", "Any", "Any", "(Optional)", mutableListOf<String>(), mutableListOf<String>(), mutableListOf<String>()),
    GptResponse("default", "default response", 0,"default", listOf(
        GptChoices(0,
            GptMessage("0", "0"),"finish")
    ),
        GptUsage(1, 1, 2) ),
    ParsedResponse("1","2", "3", "4", "5")))



@Composable
fun Drawer(drawerState: DrawerState, scope:CoroutineScope){
// icons to mimic drawer destinations
    Realm.init(LocalContext.current)
    lateinit var realm2: Realm
    val navController = rememberNavController()
    //val items = remember{ mutableStateListOf(testRecipe, testRecipe2, testRecipe3)}
    val config = RealmConfiguration.Builder().name("default1")
        .schemaVersion(0)
        .deleteRealmIfMigrationNeeded()
        .allowWritesOnUiThread(true)
        .build()
    Realm.setDefaultConfiguration(config)
    runBlocking { realm2 = Realm.getInstance(config)}


    //val realm: Realm = Realm.open(config)
    val items: RealmResults<SavedRecipe> = realm2.where<SavedRecipe>().findAll()


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
            NavMain(navController, items.toList(), realm2)


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
            holder = input
            navController.navigate(Paths.Recipe.Path)
            scope.launch { drawerState.close()}
            //contentLocation="Recipe"

            //open recipe
        },
        modifier  = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )


}



@Composable
fun Settings(){
    val context = LocalContext.current
    val store = UserStore(context)
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    var imperial = remember { mutableStateOf(false) }
    var fahrenheit = remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    var allergensOpen by remember { mutableStateOf(false) }

    val allergens = remember { mutableStateListOf<String>() }

    var dd1Expanded by remember { mutableStateOf(false) }
    val skillLevel = listOf("Beginner", "Intermediate", "Expert")
    var skillLevelIndex by remember { mutableStateOf(0) }

    var saveMessage by remember{ mutableStateOf(false)}

    val loadedData = runBlocking { context.dataStore.data.first() }
    loadedData[savedPreferences]?.let { Log.d("saved preferences2", it) }

    loadedData[savedPreferences]?.let{
        val retrievedData = Json.decodeFromString<Settings>(it)
        imperial.value=retrievedData.imperial
        fahrenheit.value=retrievedData.fahrenheit
        skillLevelIndex = retrievedData.skill
        retrievedData.allergens.forEach(){ allergen->
            if(!allergens.contains(allergen))allergens.add(allergen)
        }

    }

    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Use Imperial Units")
            Switch(checked = imperial.value, onCheckedChange = { imperial.value = it })

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Use Fahrenheit")
            Switch(checked = fahrenheit.value, onCheckedChange = { fahrenheit.value = it })
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Alergens / Intolerances")
            Button(onClick = {
                allergensOpen = true
            }) {
                Text(text = "Edit")
            }

        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = "Skill Level:", modifier = Modifier
                .clickable(onClick = {
                    dd1Expanded = true
                })
            )
            Text(
                text = skillLevel[skillLevelIndex],
                textAlign = TextAlign.End, modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        dd1Expanded = true
                    })
            )

            DropdownMenu(
                expanded = dd1Expanded,
                onDismissRequest = { dd1Expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                skillLevel.forEachIndexed() { index, s ->
                    DropdownMenuItem(onClick = {
                        skillLevelIndex = index

                        dd1Expanded = false
                    }, text = { Text(text = s, textAlign = TextAlign.End) },
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }



        }
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                saveMessage=true
                val toSave = Settings(imperial.value, fahrenheit.value, allergens.toList(), skillLevelIndex)
                scope.launch {
                    context.dataStore.edit { settings ->
                        settings[savedPreferences] = Json.encodeToString(toSave)
                    }
                }
            }) {
                Text(text = "Save")
            }
        }
    }
    if (saveMessage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("your settings have been saved")
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                saveMessage = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }
                }
            }

        }
    }

    if (allergensOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("Descriptive Tags:")
                        Row(modifier = Modifier
                            .padding(start = 5.dp, end=5.dp)) {
                            Text(text = "")
                            allergens.forEachIndexed() { index, s ->
                                Row() {
                                    Box(modifier = Modifier
                                        .background(ObfsuGrey)
                                        .padding(3.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            allergens.remove(s)
                                        }) {
                                        Row() {
                                            Text(s)
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(1.dp))
                                }
                            }
                        }

                        Row() {
                            val maxChar = 17
                            TextField(
                                value = text,
                                onValueChange = {
                                    if (it.length <= maxChar) text = it
                                },
                                label = { Text("Add Tags") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        allergens.add(text)
                                        text = ""
                                    }
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                allergensOpen = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }
                }
            }

        }
    }

    //save confirmation
}

@Composable
fun Recipe(holder:SavedRecipe, navController: NavHostController){
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = holder.title!!)
        Text(text = "Description")
        Text(text=holder.description!!)
        Text(text = "Ingredients")
        Text(text=holder.ingredients!!)
        Text(text = "Method")
        Text(text=holder.method!!)
        Text(text = "Notes")
        Text(text=holder.notes!!)

    }
}

@Composable
fun NewInput(
    viewModel: InputViewModel = InputViewModel(), items: List<SavedRecipe>, navController: NavHostController, realm:Realm) {
    var dd1Expanded by remember { mutableStateOf(false) }
    var dd2Expanded by remember { mutableStateOf(false) }
    var dd3Expanded by remember { mutableStateOf(false) }


    var meatExpanded by remember { mutableStateOf(false) }
    var carbExpanded by remember { mutableStateOf(false) }
    var additionalExpanded by remember { mutableStateOf(false) }

    val meatContentItems = listOf("Select...", "Yes", "Optional", "Vegetarian", "Vegan")
    val primaryMeatItems = listOf(
        "Select...",
        "Any",
        "Beef",
        "Chicken",
        "Pork",
        "Lamb",
        "Shellfish",
        "Salmon",
        "White Fish"
    )
    val primaryCarbItems =
        listOf("Select...", "Any", "Pasta", "Potato", "Rice", "Bread", "Other", "None")


    var text by remember { mutableStateOf("") }

    var cuisineText by remember { mutableStateOf("(Optional)") }

    val ingredients = remember { mutableStateListOf<String>() }
    val exclIngredients = remember { mutableStateListOf<String>() }
    val tags = remember { mutableStateListOf<String>() }


    var meatContentIndex by remember { mutableStateOf(0) }
    var primaryMeatIndex by remember { mutableStateOf(0) }
    var primaryCarbIndex by remember { mutableStateOf(0) }


    var cuisine by remember { mutableStateOf(false) }
    var addIngredientsOpen by remember { mutableStateOf(false) }
    var removeIngredientsOpen by remember { mutableStateOf(false) }
    var tagsOpen by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val store = UserStore(context)
    val loadedData = runBlocking { context.dataStore.data.first() }


    var stopper by remember{mutableStateOf(false)}
    var disclamer by remember{mutableStateOf(true)}
    loadedData[usedTokens]?.let { Log.d("tokens used", it.toString()) }
    loadedData[usedTokens]?.let { if (it>5000)stopper=true }




    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = "Meat Content:", modifier = Modifier
                    .clickable(onClick = {
                        dd1Expanded = true
                    })
                )
                Text(
                    text = meatContentItems[meatContentIndex],
                    textAlign = TextAlign.End, modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            dd1Expanded = true
                        })
                )


                DropdownMenu(
                    expanded = dd1Expanded,
                    onDismissRequest = { dd1Expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    meatContentItems.forEachIndexed() { index, s ->
                        if (index != 0) {
                            DropdownMenuItem(onClick = {
                                meatExpanded = index == 1 || index == 2
                                if (!carbExpanded) carbExpanded = index == 3 || index == 4
                                meatContentIndex = index

                                dd1Expanded = false
                            }, text = { Text(text = s, textAlign = TextAlign.End) },
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }

            }
            if (meatExpanded) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(text = "Primary Meat:",modifier = Modifier
                        .clickable(onClick = {
                            dd2Expanded = true
                        })
                    )
                    Text(
                        text = primaryMeatItems[primaryMeatIndex],
                        textAlign = TextAlign.End, modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                dd2Expanded = true
                            })
                    )

                    DropdownMenu(
                        expanded = dd2Expanded,
                        onDismissRequest = { dd2Expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        primaryMeatItems.forEachIndexed() { index, s ->
                            if (index != 0) {
                                DropdownMenuItem(onClick = {
                                    carbExpanded = true
                                    primaryMeatIndex = index
                                    dd2Expanded = false

                                }, text = { Text(text = s, textAlign = TextAlign.End) })
                            }
                        }
                    }
                }
            }
            if (carbExpanded) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(text = "Primary Carbohydrate:",modifier = Modifier
                        .clickable(onClick = {
                            dd3Expanded = true
                        })
                    )
                    Text(
                        text = primaryCarbItems[primaryCarbIndex],
                        textAlign = TextAlign.End, modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                dd3Expanded = true
                            })
                    )

                    DropdownMenu(
                        expanded = dd3Expanded,
                        onDismissRequest = { dd3Expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        primaryCarbItems.forEachIndexed() { index, s ->
                            if (index != 0) {
                                DropdownMenuItem(onClick = {
                                    additionalExpanded = true
                                    primaryCarbIndex = index
                                    dd3Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })
                            }
                        }
                    }
                }
            }
            if (additionalExpanded) {
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Cuisine:", modifier = Modifier
                                .clickable(onClick = {
                                    cuisine = true
                                })
                        )
                        Text(
                            text = cuisineText,
                            textAlign = TextAlign.End, modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    cuisine = true
                                })
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add Ingredients:")
                        Button(onClick = {
                            addIngredientsOpen = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Excluded Ingredients:")
                        Button(onClick = {
                            removeIngredientsOpen = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Descriptive Tags:")
                        Button(onClick = {
                            tagsOpen = true
                        }) {
                            Text(text = "Edit")
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            val query = Query(
                                meatContentItems[meatContentIndex],
                                primaryMeatItems[primaryMeatIndex],
                                primaryCarbItems[primaryCarbIndex],
                                cuisineText,
                                ingredients,
                                exclIngredients,
                                tags
                            )
                            var question2 = viewModel.generateQuestion(query)
                            Log.d("constructed question", question2)
                            processing=true

                            viewModel.getResponse(question2, context) { it ->
                                val recieved = SavedRecipe(QandA(query, it, viewModel.parseResponse(it)))
                                /*realm.executeTransactionAsync { realm ->
                                    realm.insert(recieved)
                                }*/
                                holder = recieved
                                //items.add(QandA(query, it, viewModel.parseResponse(it)))
                                runBlocking{
                                        context.dataStore.edit { settings ->
                                        val currentCounterValue = settings[usedTokens] ?: 0
                                        settings[usedTokens] = currentCounterValue + it.usage.total_tokens
                                    }
                                }

                                MainScope().launch {
                                    realm.executeTransactionAsync { realm ->
                                        realm.insert(recieved)
                                    }
                                    navController.navigate(Paths.Recipe.Path)
                                }


                            }


                        }) {
                            Text(text = "Generate Recipe")
                        }

                    }
                }
            }


        }
    }

    if (cuisine) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("Add Cuisine:")
                        Row() {
                            val maxChar = 17
                            TextField(
                                value = text,
                                onValueChange = {
                                    if (it.length <= maxChar) text = it
                                },
                                label = { Text("Select Cuisine") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        cuisineText = text
                                        cuisine = false
                                        text = ""

                                    }
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                cuisine = false
                                text = ""
                            }) {
                                Text(text = "Cancel")
                            }
                        }

                    }

                }
            }
        }

    }
    
    if (addIngredientsOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("Additional Ingredients:",)
                        Row() {
                            Text(text = "")
                            ingredients.forEachIndexed() { index, s ->
                                Row() {
                                    Box(modifier = Modifier
                                        .background(ObfsuGrey)
                                        .padding(3.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            ingredients.remove(s)
                                        }) {
                                        Row() {
                                            Text(s)
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(1.dp))
                                }
                            }
                        }

                        Row() {
                            val maxChar = 17
                            TextField(
                                value = text,
                                onValueChange = {
                                    if (it.length <= maxChar) text = it
                                },
                                label = { Text("add ingredients") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        ingredients.add(text)
                                        text = ""
                                    }
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                addIngredientsOpen = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }

                }
            }
        }

    }

    if (removeIngredientsOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("Excluded Ingredients:",)
                        Row() {
                            Text(text = "")
                            exclIngredients.forEachIndexed() { index, s ->
                                Row() {
                                    Box(modifier = Modifier
                                        .background(ObfsuGrey)
                                        .padding(3.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            exclIngredients.remove(s)
                                        }) {
                                        Row() {
                                            Text(s)
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(1.dp))
                                }
                            }
                        }

                        Row() {
                            val maxChar = 17
                            TextField(
                                value = text,
                                onValueChange = {
                                    if (it.length <= maxChar) text = it
                                },
                                label = { Text("exclude ingredients") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        exclIngredients.add(text)
                                        text = ""
                                    }
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                removeIngredientsOpen = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }

                }
            }
        }

    }

    if (tagsOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Text("Descriptive Tags:")
                        Row(modifier = Modifier
                            .padding(start = 5.dp, end=5.dp)) {
                            Text(text = "")
                            tags.forEachIndexed() { index, s ->
                                Row() {
                                    Box(modifier = Modifier
                                        .background(ObfsuGrey)
                                        .padding(3.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            tags.remove(s)
                                        }) {
                                        Row() {
                                            Text(s)
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(1.dp))
                                }
                            }
                        }

                        Row() {
                            val maxChar = 17
                            TextField(
                                value = text,
                                onValueChange = {
                                    if (it.length <= maxChar) text = it
                                },
                                label = { Text("Add Tags") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        tags.add(text)
                                        text = ""
                                    }
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                tagsOpen = false
                                text = ""
                            }) {
                                Text(text = "Retrun")
                            }
                        }

                    }
                }
            }

        }
    }

    if (processing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        //.width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Row(modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center){
                            Text("Please Wait")
                        }
                        Row(modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center) {
                            Indicator()
                        }
                        Row(modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center) {
                            Text("Currently generating your custom recipe")
                        }


                    }
                }
            }
        }
    }

    if (stopper) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        //.width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column() {
                        Row(modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center){
                            Text("max tokens used")
                        }
                        Row(modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center) {
                            Text("this is an early beta build with limited recipe genration. please " +
                                    "contact chris to generate more recipes")
                        }


                    }
                }
            }
        }
    }

    if (disclamer) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    /* doSomething() */
                }
                .wrapContentSize(Alignment.TopStart)
                .background(com.noxapps.dinnerroulette3.ui.theme.ObfsuGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                //.background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                //.clip(RoundedCornerShape(10.dp))
                //.border(BorderStroke(1.dp,com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange))
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(IntrinsicSize.Min)
                        .wrapContentSize(Alignment.Center)
                        .background(com.noxapps.dinnerroulette3.ui.theme.SurfaceOrange)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                com.noxapps.dinnerroulette3.ui.theme.PrimaryOrange
                            )
                        )
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("DISCLAMER:")
                        Text("This is an early beta build of this application. There will be bugs" +
                                "and stuff that's not very easy to use. Please don't hesitate to contact " +
                                "me to give me feedback. \nThis version is limited to roughly 10 recipe " +
                                "generations. You can continue to view recipes after that runs out but " +
                                "old recipes may not be carried over to new versions of the app. \n" +
                                "All recipes are generated by chat gpt, so please use descretion when" +
                                " actually cooking them, especially if you have intolerances or alergies." +
                                "The settings page has a place for you to add intollerences and alergies, " +
                                "please test this if applicable." )
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                disclamer = false
                                text = ""
                            }) {
                                Text(text = "I Understand")
                            }
                        }

                    }
                }
            }

        }
    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DinnerRoulette3Theme {
        MainScaffold()
    }
}

@Composable
fun NavMain(navController: NavHostController, items:List<SavedRecipe>, realm: Realm) {
    NavHost(navController = navController, startDestination = Paths.NewInput.Path) {
        composable(Paths.NewInput.Path) {
            NewInput(navController = navController, items = items, realm = realm)
        }
        composable(Paths.Settings.Path) { Settings() }
        composable(Paths.Recipe.Path) { Recipe(holder, navController) }
        /*...*/
    }
}