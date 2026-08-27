package com.stefdp.pterodactylpanel.screens.shared.accountsettings

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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountTab
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.activity.ActivityTab
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.apicredentials.ApiCredentialsTab
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.app.AppTab
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.sshkeys.SshKeysTab

@Composable
fun AccountSettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: AccountSettingsViewModel = viewModel(),
    update: Boolean,
    updateSwitchCategory: Boolean,
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val updateLocalLoggedUser = LocalUpdateLoggedUser.current

    fun reload() {
        viewModel.reloadUser(
            context = context,
            updateUser = updateLocalLoggedUser,
            onError = { error ->
                Notification.show(
                    activity = activity,
                    duration = 3000L
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.init(
            updateSwitchCategory = updateSwitchCategory
        )
    }

    BackHandler(
        enabled = state.backHistory.isNotEmpty()
    ) {
        viewModel.handleBack()
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
    ) {
        val tabs = remember(state.currentTab) {
            AccountTab.entries.map { accountTab ->
                Tab(
                    label = accountTab.label,
                    id = accountTab.id,
                    active = accountTab == state.currentTab
                )
            }
        }

        ScrollableTabRow(
            tabs = tabs,
            onTabClick = { tab ->
                viewModel.setCurrentTab(AccountTab.valueOf(tab.id.uppercase()))
            },
            enabled = !state.isLoading
        )

        var refreshIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload()

                refreshIndex++
            }
        ) {
            Column(
                modifier = Modifier.waterfallPadding()
            ) {
                saveableStateHolder.SaveableStateProvider(key = state.currentTab) {
                    when (state.currentTab) {
                        AccountTab.ACCOUNT -> {
                            AccountTab(
                                context = context,
                                activity = activity,
                                refreshIndex = refreshIndex
                            )
                        }

                        AccountTab.API_CREDENTIALS -> {
                            ApiCredentialsTab(
                                context = context,
                                activity = activity,
                                refreshIndex = refreshIndex
                            )
                        }

                        AccountTab.SSH_KEYS -> {
                            SshKeysTab(
                                context = context,
                                activity = activity,
                                refreshIndex = refreshIndex
                            )
                        }

                        AccountTab.ACTIVITY -> {
                            ActivityTab(
                                context = context,
                                activity = activity,
                                refreshIndex = refreshIndex
                            )
                        }

                        AccountTab.APP -> {
                            AppTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                update = update,
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class AccountTab(
    val id: String,
    val label: String
) {
    ACCOUNT(
        id = "account",
        label = "Account"
    ),

    API_CREDENTIALS(
        id = "api_credentials",
        label = "API Credentials"
    ),

    SSH_KEYS(
        id = "ssh_keys",
        label = "SSH Keys"
    ),

    ACTIVITY(
        id = "activity",
        label = "Activity"
    ),

    APP(
        id = "app",
        label = "App"
    )
}