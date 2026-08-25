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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.toRoute
import com.stefdp.pterodactylpanel.components.Header
import com.stefdp.pterodactylpanel.components.Sidebar
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.screens.AccountSettingsScreen
import com.stefdp.pterodactylpanel.screens.ApplicationLocationScreen
import com.stefdp.pterodactylpanel.screens.ApplicationLocationsScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNestEggScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNestScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNestsScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNodesScreen
import com.stefdp.pterodactylpanel.screens.ApplicationServerScreen
import com.stefdp.pterodactylpanel.screens.ApplicationServersScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUserScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUsersScreen
import com.stefdp.pterodactylpanel.screens.ClientServerScreen
import com.stefdp.pterodactylpanel.screens.ClientServersScreen
import com.stefdp.pterodactylpanel.screens.LoadingScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.location.ApplicationLocationScreen
import com.stefdp.pterodactylpanel.screens.application.locations.ApplicationLocationsScreen
import com.stefdp.pterodactylpanel.screens.application.node.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.application.nodes.ApplicationNodesScreen
import com.stefdp.pterodactylpanel.screens.application.server.ApplicationServerScreen
import com.stefdp.pterodactylpanel.screens.application.servers.ApplicationServersScreen
import com.stefdp.pterodactylpanel.screens.application.user.ApplicationUserScreen
import com.stefdp.pterodactylpanel.screens.application.users.ApplicationUsersScreen
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerScreen
import com.stefdp.pterodactylpanel.screens.client.servers.ClientServersScreen
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.AccountSettingsScreen
import com.stefdp.pterodactylpanel.screens.shared.loading.LoadingScreen
import com.stefdp.pterodactylpanel.screens.shared.login.LoginScreen
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

// TODO: when admin side is done, add "show others' servers" switch in main servers list when application api key is provided
// TODO: in tabbed screens, use BackHandler so the back button switches between previous tabs instead of previous screen

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

                val state by viewModel.state.collectAsState()

                val networkMonitor = NetworkMonitor(context)

                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        viewModel.updateLoggedUser(context)
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                CompositionLocalProvider(
                    LocalLoggedUser provides state.loggedUser,
                    LocalUpdateLoggedUser provides viewModel::updateLoggedUser,
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
                                modifier = Modifier.fillMaxSize(),
                                drawerState = drawerState,
                                drawerContent = {
                                    Sidebar(
                                        modifier = Modifier.padding(
                                            top = innerPadding.calculateTopPadding(),
                                            bottom = innerPadding.calculateBottomPadding()
                                        ),
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
                                gesturesEnabled = currentDestination?.route !in invalidRoutes
                            ) {
                                AppNavigation(
                                    navController = navController,
                                    context = context,
                                    activity = activity,
                                    innerPadding = innerPadding
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
    activity: FragmentActivity,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = if (IS_DEBUG) DEBUG_SCREEN else LoadingScreen,
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
            LoadingScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<LoginScreen> {
            LoginScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<AccountSettingsScreen> {
            AccountSettingsScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<ClientServersScreen> {
            ClientServersScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<ClientServerScreen> { backStackEntry ->
            val clientServerScreen = backStackEntry.toRoute<ClientServerScreen>()

            ClientServerScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding,
                serverId = clientServerScreen.serverId,
                directory = clientServerScreen.directory,
                switchToDatabases = clientServerScreen.switchToDatabases,
                isSuspended = clientServerScreen.isServerSuspended,
                isInstalling = clientServerScreen.isServerInstalling,
                isTransferring = clientServerScreen.isServerTransferring,
                isNodeUnderMaintenance = clientServerScreen.isServerNodeUnderMaintenance,
                isRestoringBackup = clientServerScreen.isServerRestoringBackup,
                isServerOwner = clientServerScreen.isServerOwner
            )
        }

        composable<ApplicationLocationsScreen> {
            ApplicationLocationsScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<ApplicationLocationScreen> { backStackEntry ->
            val applicationLocationScreen = backStackEntry.toRoute<ApplicationLocationScreen>()

            ApplicationLocationScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding,
                locationId = applicationLocationScreen.locationId
            )
        }

        composable<ApplicationNodesScreen> {
            ApplicationNodesScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<ApplicationNodeScreen> { backStackEntry ->
            val applicationNodeScreen = backStackEntry.toRoute<ApplicationNodeScreen>()

            ApplicationNodeScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding,
                nodeId = applicationNodeScreen.nodeId
            )
        }

        composable<ApplicationServersScreen> {
            ApplicationServersScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<ApplicationServerScreen> { backStackEntry ->
            val applicationServerScreen = backStackEntry.toRoute<ApplicationServerScreen>()

            ApplicationServerScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding,
                serverId = applicationServerScreen.serverId
            )
        }

        composable<ApplicationUsersScreen> {
            ApplicationUsersScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding
            )
        }

        composable<ApplicationUserScreen> { backStackEntry ->
            val applicationUserScreen = backStackEntry.toRoute<ApplicationUserScreen>()

            ApplicationUserScreen(
                navController = navController,
                activity = activity,
                context = context,
                innerPadding = innerPadding,
                userId = applicationUserScreen.userId
            )
        }

        composable<ApplicationNestsScreen> {
            // TODO: add application nests screen
        }

        composable<ApplicationNestScreen> { backStackEntry ->
            val applicationNestScreen = backStackEntry.toRoute<ApplicationNestScreen>()

//            applicationNestScreen.nestId

            // TODO: add application nest screen
        }

        composable<ApplicationNestEggScreen> { backStackEntry ->
            val applicationNestEggScreen = backStackEntry.toRoute<ApplicationNestEggScreen>()

//            applicationNestEggScreen.nestId
//            applicationNestEggScreen.eggId

            // TODO: add application nest egg screen
        }
    }
}