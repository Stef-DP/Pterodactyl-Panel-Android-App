package com.stefdp.pterodactylpanel

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stefdp.pterodactylpanel.components.Header
import com.stefdp.pterodactylpanel.components.Sidebar
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.screens.HomeScreen
import com.stefdp.pterodactylpanel.screens.LoadingScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.NetworkMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

const val BASE_CORNER_RADIUS = 10

val LocalLoggedUser = compositionLocalOf<User?> { null }
val LocalUpdateLoggedUser = compositionLocalOf<suspend (context: Context) -> Result<User>> {
    {
        Result.failure(
            Exception("Placeholder")
        )
    }
}

class MainActivity : FragmentActivity() {
    private var isAppReady by mutableStateOf(false)

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !isAppReady
        }

        lifecycleScope.launch {
            delay(100L.milliseconds)
            isAppReady = true
        }

        val context = applicationContext

        enableEdgeToEdge()

        setContent {
            PterodactylPanelTheme {
                val activity = this@MainActivity

                val navController = rememberNavController()

                val networkMonitor = NetworkMonitor(context)

                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        // TODO: login
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                CompositionLocalProvider(
                    // TODO: for later, for global data
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    val invalidRoutes = listOf(
                        LoginScreen::class.qualifiedName,
                        LoadingScreen::class.qualifiedName,
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            val isInvalid = invalidRoutes.any { routeName ->
                                currentDestination?.route?.startsWith(routeName ?: "") == true
                            }

                            if (!isInvalid) {
                                Header(
                                    activity = activity,
                                    context = context,
                                    navController = navController,
                                    onMenuClick = {
                                        coroutineScope.launch {
                                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ModalNavigationDrawer(
                                modifier = Modifier.padding(innerPadding),
                                drawerState = drawerState,
                                drawerContent = {
                                    Sidebar(
                                        onItemClick = { screen ->
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate(screen)
                                        },
                                        navController = navController,
                                        closeSidebar = {
                                            coroutineScope.launch { drawerState.close() }
                                        }
                                    )
                                },
//                                gesturesEnabled = currentDestination?.route !in invalidRoutes
                            ) {
                                AppNavigation(
                                    navController = navController,
                                    context = context,
                                    activity = activity
                                )
                            }
                        }
                    }
                }

                if (!isConnected) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.wifi_off),
                                    contentDescription = "No internet connection",
                                    modifier = Modifier.size(50.dp)
                                )

                                Spacer(
                                    modifier = Modifier.height(20.dp)
                                )

                                Text(
                                    text = "No Internet Connection",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    NavHost(
        navController = navController,
        startDestination = LoadingScreen,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
        },
        exitTransition = {
            fadeOut(tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
        }
    ) {
        composable<LoadingScreen> {
            // TODO: add loading screen
        }

        composable<LoginScreen> {
            // TODO: add login screen
        }

        composable<HomeScreen> {
            // TODO: add home screen
        }

        // TODO: add all other screens
    }
}