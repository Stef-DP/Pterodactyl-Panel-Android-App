package com.stefdp.pterodactylpanel.screens.application.users.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.application.users.ApplicationUsersUiState
import com.stefdp.pterodactylpanel.screens.application.users.ApplicationUsersViewModel
import com.stefdp.pterodactylpanel.screens.application.users.Languages

@Composable
fun CreateUserPopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationUsersUiState,
    viewModel: ApplicationUsersViewModel,
) {
    Popup(
        showPopup = state.showCreateUserPopup,
        onDismissRequest = {
            viewModel.hideCreateUserPopup()
        },
    ) {
        Text(
            text = "Create User",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )

        Text(
            text = "Add a new user to the system",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

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
                value = state.newUserUsername,
                onValueChange = {
                    viewModel.setNewUserUsername(it)
                },
                label = "Username",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newUserFirstName,
                onValueChange = {
                    viewModel.setNewUserFirstName(it)
                },
                label = "Client First Name",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newUserLastName,
                onValueChange = {
                    viewModel.setNewUserLastName(it)
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
                selectedIds = state.newUserDefaultLanguage,
                onSelectionChange = {
                    viewModel.setNewUserDefaultLanguage(it)
                },
                description = "The default language to use when rendering the Panel for this user",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                enabled = !state.isLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

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
                checked = state.newUserIsAdmin,
                onCheckedChange = {
                    viewModel.setNewUserIsAdmin(it)
                },
                label = "Administrator",
                description = "Enabling this gives a user full administrative access",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                enabled = !state.isLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Providing a user password is optional. New user emails prompt users to create a password the first time they login. If a password is provided here you will need to find a different method of providing it to the user"
                )
            }

            TextInput(
                value = state.newUserPassword,
                onValueChange = {
                    viewModel.setNewUserPassword(it)
                },
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideCreateUserPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = state.users != null
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createUser(
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
                                    text = "User created successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled =
                    state.users != null &&
                    !state.isLoading &&
                    state.newUserEmail.text.trim().isNotBlank() &&
                    state.newUserUsername.text.trim().isNotBlank() &&
                    state.newUserFirstName.text.trim().isNotBlank() &&
                    state.newUserLastName.text.trim().isNotBlank() &&
                    state.newUserDefaultLanguage.isNotEmpty()
            ) {
                Text("Create User")
            }
        }
    }
}