package com.stefdp.pterodactylpanel.screens.client.accountsettings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.stefdp.pterodactylpanel.IS_DEBUG
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.account.AccountTab
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.apicredentials.ApiCredentialsTab
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.sshkeys.SshKeysTab

@Composable
fun ClientAccountSettingsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ClientAccountSettingsViewModel = viewModel()
) {
    // TODO: remove the is debug check later
    if (!IS_DEBUG) {
        val localLoggedUser = LocalLoggedUser.current

        if (localLoggedUser == null) {
            navController.navigate(LoginScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
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

                        else -> {
                            Text("WIP")
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
    )
}