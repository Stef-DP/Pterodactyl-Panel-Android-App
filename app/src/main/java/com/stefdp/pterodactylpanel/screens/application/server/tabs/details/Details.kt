package com.stefdp.pterodactylpanel.screens.application.server.tabs.details

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gravatar.types.Email
import com.gravatar.ui.components.atomic.Avatar
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.utils.NameRegex
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun DetailsTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationServerDetailsTabViewModel = viewModel(),
    server: ApplicationServer?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(server?.attributes?.id, refreshIndex) {
        if (server == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(
            context = context,
            server = server
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState)
    ) {
        Container(
            title = {
                Text(
                    text = "Base Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.serverName,
                onValueChange = {
                    if (!it.text.trim().matches(NameRegex)) return@TextInput

                    viewModel.setServerName(it)
                },
                enabled = !state.isLoading,
                label = "Server Name",
                description = "Character limits: `a-zA-Z0-9_-` and `[Space]`",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                required = true,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.serverExternalIdentifier,
                onValueChange = {
                    viewModel.setServerExternalIdentifier(it)
                },
                enabled = !state.isLoading,
                label = "External Identifier",
                description = "Leave empty to not assign an external identifier for this server. The external ID should be unique to this server and not be in use by any other servers",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            )

            Select(
                options = state.serverOwnerSuggestions.map { (_, user) ->
                    SelectOption(
                        id = user.id.toString(),
                        searchLabel = user.email,
                        label = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Avatar(
                                    email = Email(user.email),
                                    size = 40.dp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${user.firstName} ${user.lastName}"
                                    )

                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(user.email)
                                            }

                                            append(" - ${user.username}")
                                        }
                                    )
                                }
                            }
                        },
                    )
                },
                selectedIds = state.serverOwner,
                onSelectionChange = {
                    viewModel.setServerOwner(
                        context = context,
                        owner = it
                    )
                },
                optionsLoading = state.serverOwnerSuggestionsLoading,
                optionsLoadingText = "Searching...",
                label = "Server Owner",
                description = "You can change the owner of this server by changing this field to an email matching another use on this system. If you do this a new daemon security token will be generated automatically",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                enabled = !state.isLoading,
                searchable = true,
                searchPlaceholder = if (state.serverOwnerSearchQuery.length < 2) {
                    "Please enter 2 or more characters"
                } else {
                    "Search..."
                },
                onSearchQueryChange = {
                    viewModel.setServerOwnerSearchQuery(
                        context = context,
                        query = it
                    )
                },
                hideNoOptionsText = state.serverOwnerSearchQuery.length < 2,
            )

            TextInput(
                value = state.serverDescription,
                onValueChange = {
                    viewModel.setServerDescription(it)
                },
                label = "Server Description",
                description = "A brief description of this server",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                enabled = !state.isLoading
            )

            Button(
                onClick = {
                    viewModel.updateServer(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Successfully updated server details"
                                )
                            }
                        },
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
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(
                    text = "Update Details"
                )
            }
        }
    }
}