package com.stefdp.pterodactylpanel.screens.client.server.tabs.databases

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.DatabaseDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.popups.ConfirmDatabaseDeletionPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.popups.CreateDatabasePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.popups.DatabaseDetailsPopup
import com.stefdp.pterodactylpanel.utils.hasPermission
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun DatabasesTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerDatabasesTabViewModel = viewModel(),
    server: GetServerResponse?
) {
    LaunchedEffect(server) {
        viewModel.init(server)

        viewModel.updateDatabases(
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
            onSuccess = {}
        )
    }

    val state by viewModel.state.collectAsState()

    if (server?.attributes?.featureLimits?.databases == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Databases cannot be created for this server",
                textAlign = TextAlign.Center
            )
        }

        return
    }

    CreateDatabasePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    DatabaseDetailsPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
        hasViewPasswordPermission = hasPermission(
            isServerOwner = state.isServerOwner,
            userPermissions = state.userPermissions,
            requiredPermission = ServerSubuser.Permissions.DATABASE_VIEW_PASSWORD
        ),
        hasUpdatePermission = hasPermission(
            isServerOwner = state.isServerOwner,
            userPermissions = state.userPermissions,
            requiredPermission = ServerSubuser.Permissions.DATABASE_UPDATE
        )
    )

    ConfirmDatabaseDeletionPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val allocatedDatabases = state.databases.size
            val databaseLimit = server?.attributes?.featureLimits?.databases ?: 0

            Text(
                text = "$allocatedDatabases of $databaseLimit databases have been allocated to this server",
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            if (
                hasPermission(
                    isServerOwner = state.isServerOwner,
                    userPermissions = state.userPermissions,
                    requiredPermission = ServerSubuser.Permissions.DATABASE_CREATE
                )
            ) {
                Button(
                    onClick = {
                        viewModel.showCreateDatabasePopup()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = allocatedDatabases < databaseLimit && !state.isLoading
                ) {
                    Text(
                        text = "New Database"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        if (state.databases.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "It looks like you have no databases",
                    textAlign = TextAlign.Center
                )
            }

            return@Column
        }

        val lazyColumnListState = rememberLazyListState()

        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier
                .fillMaxSize()
                .verticalLazyScrollbar(
                    listState = lazyColumnListState
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.isLoading) {
                items(10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmerable(
                                enabled = true,
                                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                                height = 60.dp
                            )
                    )
                }

                return@LazyColumn
            }

            items(state.databases.size) { index ->
                val database = state.databases[index]

                DatabaseDisplay(
                    database = database,
                    hasDeletePermission = hasPermission(
                        isServerOwner = state.isServerOwner,
                        userPermissions = state.userPermissions,
                        requiredPermission = ServerSubuser.Permissions.DATABASE_DELETE
                    ),
                    onShowDatabaseDetails = {
                        viewModel.setDatabaseToShowDetails(database.attributes.id)
                    },
                    onDeleteDatabase = {
                        viewModel.setDatabaseToDelete(database.attributes.id)
                    }
                )
            }
        }
    }
}