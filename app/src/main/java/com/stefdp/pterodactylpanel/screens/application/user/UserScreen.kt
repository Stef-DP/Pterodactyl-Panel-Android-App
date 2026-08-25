package com.stefdp.pterodactylpanel.screens.application.user

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.ApplicationUserScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUsersScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.users.Languages
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun ApplicationUserScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    userId: Long,
    viewModel: ApplicationUserViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    fun reload(isRefresh: Boolean = false) {
        viewModel.init(
            context = context,
            userId = userId,
            isRefresh = isRefresh,
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
            },
        )
    }

    LaunchedEffect(userId) {
        reload()
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            reload(isRefresh = true)
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .verticalScrollWithScrollbar(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${state.user?.attributes?.firstName} ${state.user?.attributes?.lastName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .alignByBaseline()
                            .shimmerable(
                                enabled = state.user == null
                            )
                    )

                    Text(
                        text = state.user?.attributes?.username ?: "Loading...",
                        modifier = Modifier
                            .alignByBaseline()
                            .shimmerable(
                                enabled = state.user == null
                            )
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Identity",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    TextInput(
                        value = state.newUserEmail,
                        onValueChange = {
                            viewModel.setNewUserEmail(it)
                        },
                        label = "Email",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )

                    TextInput(
                        value = state.userUsername,
                        onValueChange = {
                            viewModel.setUserUsername(it)
                        },
                        label = "Username",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )

                    TextInput(
                        value = state.userFirstName,
                        onValueChange = {
                            viewModel.setUserFirstName(it)
                        },
                        label = "Client First Name",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )

                    TextInput(
                        value = state.userLastName,
                        onValueChange = {
                            viewModel.setUserLastName(it)
                        },
                        label = "Client Last Name",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )

                    Select(
                        options = Languages.entries.map { language ->
                            SelectOption(
                                id = language.code,
                                label = {
                                    Text(
                                        text = language.label
                                    )
                                }
                            )
                        },
                        label = "Default Language",
                        selectedIds = state.userDefaultLanguage,
                        onSelectionChange = {
                            viewModel.setUserDefaultLanguage(it)
                        },
                        description = "The default language to use when rendering the Panel for this user",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        enabled = !state.isLoading
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    TextInput(
                        value = state.userPassword,
                        onValueChange = {
                            viewModel.setUserPassword(it)
                        },
                        label = "Password",
                        description = "Leave blank to keep this user's password the same. User will not receive any notification if password is changed",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Permissions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    Switch(
                        checked = state.userIsAdmin,
                        onCheckedChange = {
                            viewModel.setUserIsAdmin(it)
                        },
                        label = "Administrator",
                        description = "Enabling this gives a user full administrative access",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        enabled = !state.isLoading
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Delete User",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    Text(
                        text = "There must be no servers associated with this account in order for it to be deleted"
                    )

                    Button(
                        onClick = {
                            viewModel.deleteUser(
                                context = context,
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
                                },
                                onSuccess = {
                                    navController.navigate(ApplicationUsersScreen) {
                                        popUpTo<ApplicationUserScreen> { inclusive = true }
                                    }
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        buttonType = ButtonType.ERROR,
                        enabled = !state.isLoading && !state.ownsServers
                    ) {
                        Text(
                            text = "Delete User"
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {
                    viewModel.updateUser(
                        context = context,
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
                        },
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "User updated successfully",
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    !state.isLoading &&
                            state.newUserEmail.text.trim().isNotBlank() &&
                            state.userUsername.text.trim().isNotBlank() &&
                            state.userFirstName.text.trim().isNotBlank() &&
                            state.userLastName.text.trim().isNotBlank() &&
                            state.userDefaultLanguage.isNotEmpty()
            ) {
                Text(
                    text = "Update user"
                )
            }
        }
    }
}