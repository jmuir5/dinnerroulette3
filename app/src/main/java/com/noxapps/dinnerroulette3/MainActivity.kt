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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import com.noxapps.dinnerroulette3.ui.theme.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.serialization.decodeFromString

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saveData")
val EXAMPLE_COUNTER = stringPreferencesKey("storedResponse")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* fab click handler */ }
            ) {
                Text("Inc")
            }
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
val testRecipe=QandA(
    Query("yes", "chicken", "bread", 1,
        2,false, false, mutableListOf<String>("none"), mutableListOf("none")),
    GptResponse("tester", "test response", 0,"test", listOf(GptChoices(0,
        GptMessage("role", "[title]Pork and Potato Stew;;;\n" +
                "[desc]This hearty stew is perfect for a cozy night in. Tender pork and potatoes are simmered in a savory tomato-based sauce until everything is melt-in-your-mouth delicious.;;;\n" +
                "[ingr]\n" +
                "- 500g pork shoulder, cut into cubes\n" +
                "- 1 large onion, chopped\n" +
                "- 3 cloves garlic, minced\n" +
                "- 3 large potatoes, peeled and cut into cubes\n" +
                "- 1 can diced tomatoes (400g)\n" +
                "- 1 cup chicken stock (250ml)\n" +
                "- 1 tbsp tomato paste\n" +
                "- 1 tsp paprika\n" +
                "- 1 tsp dried thyme\n" +
                "- Salt and pepper, to taste\n" +
                "- 2 tbsp vegetable oil\n" +
                ";;;\n" +
                "[method]\n" +
                "1. Heat the oil in a large pot over medium-high heat. Add the pork and cook until browned on all sides, about 5 minutes.\n" +
                "2. Add the onion and garlic to the pot and cook until softened, about 3 minutes.\n" +
                "3. Stir in the diced tomatoes, chicken stock, tomato paste, paprika, thyme, salt, and pepper. Bring the mixture to a boil.\n" +
                "4. Reduce the heat to low, cover the pot, and simmer the stew for 1 hour.\n" +
                "5. Add the potatoes to the pot and continue to simmer the stew for an additional 30 minutes, or until the potatoes are tender and the pork is cooked through.\n" +
                "6. Serve the stew hot, garnished with fresh herbs if desired.\n" +
                ";;;\n" +
                "[notes]This stew can be made a day ahead of time and reheated for an easy weeknight meal. Feel free to add any additional vegetables you have on hand, such as carrots or celery. Enjoy![fin]"),"finish"
    )),GptUsage(1, 1, 2) ), "Pork and Potato Stew - beginner")
val testRecipe2=QandA(
    Query("yes", "chicken", "bread", 1,
        2,false, false, mutableListOf<String>("none"), mutableListOf("none")),
    GptResponse("tester", "test response", 0,"test", listOf(GptChoices(0,
        GptMessage("role", "[title]Pork and Potato Skillet;;\n" +
                "[desc]This Pork and Potato skillet recipe is a savory, one-pan meal that is perfect for a quick and easy dinner. The potatoes are cooked until crispy and the pork is juicy and flavorful.;;\n" +
                "[ingr]\n" +
                "- 500g pork tenderloin, sliced into 1/2 inch pieces\n" +
                "- 1/2 tsp salt\n" +
                "- 1/4 tsp black pepper\n" +
                "- 2 tbsp olive oil\n" +
                "- 1 onion, chopped\n" +
                "- 3 cloves garlic, minced\n" +
                "- 1 red bell pepper, sliced\n" +
                "- 1 green bell pepper, sliced\n" +
                "- 500g potatoes, peeled and sliced into 1/4 inch pieces\n" +
                "- 1 tsp dried thyme\n" +
                "- 1 cup chicken broth\n" +
                "- 2 tbsp chopped parsley\n" +
                "- 2 tbsp chopped scallions\n" +
                "- Salt and pepper to taste\n" +
                ";;;\n" +
                "[method]\n" +
                "1. Season the pork with 1/2 tsp salt and 1/4 tsp black pepper.\n" +
                "2. Heat 1 tbsp of olive oil in a large skillet over medium-high heat. Add the pork and cook for 2-3 minutes on each side, until browned. Remove the pork from the skillet and set aside.\n" +
                "3. Add the remaining 1 tbsp of olive oil to the skillet. Add the onion and garlic and cook for 2-3 minutes, until softened.\n" +
                "4. Add the sliced peppers and cook for 2-3 minutes, until softened.\n" +
                "5. Add the sliced potatoes and thyme to the skillet and stir to combine.\n" +
                "6. Pour the chicken broth over the potato mixture and bring to a boil. Reduce the heat to medium-low and simmer for 15-20 minutes, until the potatoes are tender and the liquid has reduced by half.\n" +
                "7. Add the pork back to the skillet and cook for an additional 5-10 minutes, until the pork is cooked through and the liquid has thickened.\n" +
                "8. Garnish with chopped parsley and scallions and season with salt and pepper to taste.\n" +
                "9. Serve hot and enjoy your Pork and Potato Skillet!\n" +
                ";;;\n" +
                "[notes]If you don't have chicken broth, you can use vegetable broth or water instead. You can also add other vegetables like carrots, zucchini or mushrooms to the skillet."),"finish"
    )),GptUsage(1, 1, 2) ), "Pork and Potato Skillet - intermediate")
val testRecipe3=QandA(
    Query("yes", "chicken", "bread", 1,
        2,false, false, mutableListOf<String>("none"), mutableListOf("none")),
    GptResponse("tester", "test response", 0,"test", listOf(GptChoices(0,
        GptMessage("role", "[title]Pork and Potato Stew;;;\n" +
                "[desc]A hearty and comforting stew made with tender pork and creamy potatoes.;;;\n" +
                "[ingr]\n" +
                "- 1 kg pork shoulder, cut into cubes\n" +
                "- 1 kg potatoes, peeled and cut into chunks\n" +
                "- 2 onions, chopped\n" +
                "- 4 cloves of garlic, minced\n" +
                "- 2 bell peppers, seeded and chopped\n" +
                "- 2 cans of chopped tomatoes\n" +
                "- 2 cups of chicken stock\n" +
                "- 2 tbsp of tomato paste\n" +
                "- 2 tbsp of olive oil\n" +
                "- 1 tsp of smoked paprika\n" +
                "- 1 tsp of dried thyme\n" +
                "- Salt and pepper to taste\n" +
                ";;;\n" +
                "[method]\n" +
                "1. Heat the olive oil in a large pot or Dutch oven over medium heat. Add the pork and cook until browned on all sides, about 5 minutes.\n" +
                "\n" +
                "2. Remove the pork from the pot and set it aside. Add the onions and garlic to the pot and cook until softened, about 5 minutes.\n" +
                "\n" +
                "3. Add the bell peppers, smoked paprika, and thyme to the pot and cook for another 5 minutes.\n" +
                "\n" +
                "4. Add the pork back to the pot, along with the chopped tomatoes, tomato paste, and chicken stock. Season with salt and pepper to taste.\n" +
                "\n" +
                "5. Bring the stew to a boil, then reduce the heat to low and let it simmer for 1 hour.\n" +
                "\n" +
                "6. Add the potatoes to the pot and continue to simmer for another 30 minutes, or until the potatoes are tender and the pork is cooked through.\n" +
                "\n" +
                "7. Serve the stew hot, garnished with fresh parsley or cilantro, if desired.\n" +
                ";;;\n" +
                "[notes] This stew can be made ahead of time and reheated for an easy weeknight dinner. It also freezes well, so consider making a double batch and freezing the leftovers for later. Enjoy!;;;"),"finish"
    )),GptUsage(1, 1, 2) ), "Pork and Potato Stew - expert")



@Composable
fun Drawer(drawerState: DrawerState, scope:CoroutineScope){
// icons to mimic drawer destinations
    val navController = rememberNavController()
    var contentLocation by remember{ mutableStateOf("NewInput")}
    val items = remember{ mutableStateListOf(testRecipe, testRecipe2, testRecipe3)}


    val selectedItem = remember { mutableStateOf(items[0]) }
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
                        if (contentLocation.toString()=="NewInput") scope.launch { drawerState.close()}
                        else {
                            navController.navigate(Paths.NewInput.Path)
                            /*{
                                navOptions {
                                    popUpTo(Paths.Settings.Path) {
                                        inclusive = true
                                    }
                                }
                            }*/
                            contentLocation="NewInput"
                            scope.launch { drawerState.close()}
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        if (contentLocation=="Settings") scope.launch { drawerState.close()}
                        else {
                            navController.navigate(Paths.Settings.Path)
                                /*{
                                navOptions {
                                    popUpTo(Paths.Settings.Path) {
                                        inclusive = true
                                    }
                                }
                            }*/
                            contentLocation="Settings"
                            scope.launch { drawerState.close()}
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(12.dp))

                items.forEachIndexed() { index, item ->
                    DrawerRecipeItem(input = item, index = index, contentLocation= contentLocation, navController = navController, scope = scope, drawerState = drawerState)
                }

                Spacer(Modifier.height(12.dp))



            }
        },
        content = {
            NavMain(navController, items)


        }
    )
}


@Composable
fun DrawerRecipeItem(input:QandA, index:Int, contentLocation:String, navController: NavHostController, scope:CoroutineScope, drawerState:DrawerState){
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Article, contentDescription = null) },
        label = { Text(text = input.name) },
        selected = false,
        onClick = {
            navController.navigate(Paths.Recipe.Path+"/$index")
            scope.launch { drawerState.close()}
            //contentLocation="Recipe"

            //open recipe
        },
        modifier  = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )


}

@Composable
fun Settings(){
    Text(text = "Settings Page")
}

@Composable
fun Recipe(data:String, items:MutableList<QandA>, navController: NavHostController){
    val retrievedData = items[data.toInt()]
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = "Recipe Page")
        Text(text = "Recipe:${retrievedData.name}")
        Text(text=retrievedData.answer.choices[0].message.content)
    }
}

@Composable
fun NewInput(
    viewModel: InputViewModel = InputViewModel(), items: MutableList<QandA>, navController: NavHostController
) {
    var dd1Expanded by remember { mutableStateOf(false) }
    var dd2Expanded by remember { mutableStateOf(false) }
    var dd3Expanded by remember { mutableStateOf(false) }
    var dd4Expanded by remember { mutableStateOf(false) }
    var dd5Expanded by remember { mutableStateOf(false) }

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
    val triStateItems = listOf("Optional", "Yes", "No")

    var text by remember { mutableStateOf("") }

    val ingredients = remember { mutableStateListOf<String>() }
    val tags = remember { mutableStateListOf<String>() }


    var meatContentIndex by remember { mutableStateOf(0) }
    var primaryMeatIndex by remember { mutableStateOf(0) }
    var primaryCarbIndex by remember { mutableStateOf(0) }
    var spiceIndex by remember { mutableStateOf(0) }
    var cheeseIndex by remember { mutableStateOf(0) }

    var glutenChecked by remember { mutableStateOf(false) }
    var lactoseChecked by remember { mutableStateOf(false) }

    var ingredientsOpen by remember { mutableStateOf(false) }
    var tagsOpen by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val store = UserStore(context)
    val tokenText = store.getAccessToken.collectAsState(initial = "").value
    /*items.add(QandA(
        Query("yes", "chicken", "bread", 1, 2,false, false, mutableListOf<String>("none"), mutableListOf("none")),
        Json.decodeFromString<GptResponse>(tokenText), "last recieved recipe"))*/
    Log.e("text init", tokenText.toString())



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
                Text(text = "Meat Content:")
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
                    Text(text = "Primary Meat:")
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
                    Text(text = "Primary Carbohydrate:")
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
                        Text(text = "Spice Content:")
                        Text(
                            text = triStateItems[spiceIndex],
                            textAlign = TextAlign.End, modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    dd4Expanded = true
                                })
                        )

                        DropdownMenu(
                            expanded = dd4Expanded,
                            onDismissRequest = { dd4Expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            triStateItems.forEachIndexed() { index, s ->
                                DropdownMenuItem(onClick = {
                                    spiceIndex = index
                                    dd4Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })

                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(text = "Cheese Content:")
                        Text(
                            text = triStateItems[cheeseIndex],
                            textAlign = TextAlign.End, modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    dd5Expanded = true
                                })
                        )

                        DropdownMenu(
                            expanded = dd5Expanded,
                            onDismissRequest = { dd5Expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            triStateItems.forEachIndexed() { index, s ->
                                DropdownMenuItem(onClick = {
                                    cheeseIndex = index
                                    dd5Expanded = false
                                }, text = { Text(text = s, textAlign = TextAlign.End) })

                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Gluten Free:")
                        Switch(checked = glutenChecked, onCheckedChange = { glutenChecked = it })

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Lactose Free:")
                        Switch(checked = lactoseChecked, onCheckedChange = { lactoseChecked = it })

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Additional Ingredients:")
                        Button(onClick = {
                            ingredientsOpen = true
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
                                spiceIndex,
                                cheeseIndex,
                                glutenChecked,
                                lactoseChecked,
                                ingredients,
                                tags
                            )
                            var question2 = viewModel.generateQuestion(query)
                            Log.d("constructed question", question2)
                            processing=true

                            viewModel.getResponse(question2) { it ->
                                Log.e("output recipe", Json.encodeToString(it))
                                runBlocking { store.saveToken(Json.encodeToString(it)) }
                                var confirmationString: String = store.getAccessToken.toString()
                                Log.e("confirmation String", confirmationString)
                                items.add(QandA(query, it, "test recieved recipe"))

                                MainScope().launch { navController.navigate(Paths.Recipe.Path + "/${items.size - 1}") }


                            }


                        }) {
                            Text(text = "Generate Recipe")
                        }

                    }
                }
            }


        }
    }
    if (ingredientsOpen) {
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
                                ClickableText(text = AnnotatedString(s), onClick = {
                                    ingredients.remove(s)
                                })

                            }
                        }

                        Row() {
                            TextField(
                                value = text,
                                onValueChange = { text = it },
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
                                ingredientsOpen = false
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
                        Row() {
                            Text(text = "")
                            tags.forEachIndexed() { index, s ->
                                ClickableText(
                                    text = AnnotatedString(s),
                                    onClick = { tags.remove(s) })
                            }
                        }

                        Row() {
                            TextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text("Add Tags") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        tags.add(text)
                                        //Log.e("tags list", tagsList.toString())
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
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DinnerRoulette3Theme {
        MainScaffold()
    }
}

@Composable
fun NavMain(navController: NavHostController, items:MutableList<QandA>) {
    NavHost(navController = navController, startDestination = Paths.NewInput.Path) {
        composable(Paths.NewInput.Path) {
            NewInput(navController = navController, items = items)
        }
        composable(Paths.Settings.Path) { Settings() }
        composable(Paths.Recipe.Path+"/{id}") {
            val data = it.arguments?.getString("id")
            if (data != null) {
                Recipe(data, items, navController)
            }
        }
        /*...*/
    }
}