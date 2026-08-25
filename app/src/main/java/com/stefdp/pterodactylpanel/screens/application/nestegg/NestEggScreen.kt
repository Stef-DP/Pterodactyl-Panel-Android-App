package com.stefdp.pterodactylpanel.screens.application.nestegg

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.configuration.ConfigurationTab
import com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.installscript.InstallScriptTab
import com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.variables.VariablesTab

@Composable
fun ApplicationNestEggScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    nestId: Long,
    eggId: Long,
    viewModel: ApplicationNestEggViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    var refreshIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    fun reload(
        isRefresh: Boolean = false,
        onReloadFinish: () -> Unit = {},
        increaseRefreshIndex: Boolean = false,
        onError: (String) -> Unit = { error ->
            Notification.show(
                activity = activity,
                duration = 3000L
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
    ) {
        viewModel.init(
            context = context,
            nestId = nestId,
            eggId = eggId,
            onError = onError,
            onReloadFinish = {
                if (isRefresh || increaseRefreshIndex) refreshIndex++

                onReloadFinish()
            },
            isRefresh = isRefresh
        )
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    LaunchedEffect(nestId, eggId) {
        reload()
    }

    BackHandler(
        enabled = state.backHistory.isNotEmpty()
    ) {
        viewModel.handleBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
    ) {
        val openInNewIcon = painterResource(R.drawable.open_in_new)

        val tabs = remember(
            localLoggedUser,
            state.currentTab,
            state.egg
        ) {
            NestEggTab.entries.map { nestEggTab ->
                Tab(
                    label = nestEggTab.label,
                    id = nestEggTab.id,
                    active = nestEggTab == state.currentTab
                )
            }
        }

        ScrollableTabRow(
            tabs = tabs,
            onTabClick = { tab ->
                viewModel.setCurrentTab(NestEggTab.valueOf(tab.id.uppercase()))
            },
            enabled = state.egg != null && !state.isLoading
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload(
                    isRefresh = true
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .waterfallPadding()
            ) {
                saveableStateHolder.SaveableStateProvider(key = state.currentTab) {
                    when (state.currentTab) {
                        NestEggTab.CONFIGURATION -> {
                            ConfigurationTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                egg = state.egg,
                                refreshIndex = refreshIndex
                            )
                        }

                        NestEggTab.VARIABLES -> {
                            VariablesTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                egg = state.egg,
                                refreshIndex = refreshIndex
                            )
                        }

                        NestEggTab.INSTALL_SCRIPT -> {
                            InstallScriptTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                egg = state.egg,
                                refreshIndex = refreshIndex
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class NestEggTab(
    val id: String,
    val label: String
) {
    CONFIGURATION(
        id = "configuration",
        label = "Configuration"
    ),

    VARIABLES(
        id = "variables",
        label = "Variables"
    ),

    INSTALL_SCRIPT(
        id = "install_script",
        label = "Install Script"
    )
}