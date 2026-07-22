package com.stefdp.pterodactylpanel.screens.client.server.tabs.databases

import android.content.ClipData
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.DatabaseDisplay
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import kotlinx.coroutines.launch

@Composable
fun DatabasesTab(
    navController: NavHostController,
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

    if (state.server?.attributes?.featureLimits?.databases == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Databases cannot be created for this server"
            )
        }

        return
    }

    val clipboardManager = LocalClipboard.current

    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = state.showCreateDatabasePopup,
        onDismissRequest = {
            viewModel.hideCreateDatabasePopup()
        },
        scrollable = true
    ) {
        Text(
            text = "Create New Database",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextInput(
                label = "Database Name",
                value = state.newDatabaseName,
                onValueChange = {
                    viewModel.setNewDatabaseName(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextInput(
                label = "Connections From",
                value = state.newDatabaseAllowedIp,
                onValueChange = {
                    viewModel.setNewDatabaseAllowedIp(it)
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.hideCreateDatabasePopup()
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        viewModel.createDatabase(
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
                                        text = "Database created successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.PRIMARY,
                    enabled = state.newDatabaseName.text.trim().length >= 3 && !state.isLoading
                ) {
                    Text("Create Database")
                }
            }
        }
    }

    Popup(
        showPopup = state.databaseToShowDetails != null,
        onDismissRequest = {
            viewModel.setDatabaseToShowDetails(null)
        },
        scrollable = true
    ) {
        val database = state.databases.find { it.attributes.id == state.databaseToShowDetails }?.attributes ?: return@Popup

        Text(
            text = "Database Connection Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val endpoint = database.host.address + ":" + database.host.port

            TextInput(
                label = "Endpoint",
                value = TextFieldValue(endpoint),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Endpoint",
                            endpoint
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val allowedIp = database.allowedIp

            TextInput(
                label = "Connections From",
                value = TextFieldValue(if (allowedIp == "%") "$allowedIp (Any IP Address)" else allowedIp),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Connections From",
                            allowedIp
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val username = database.username

            TextInput(
                label = "Username",
                value = TextFieldValue(username),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Username",
                            username
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val password = database.relationships?.password?.attributes?.password ?: "Unknown Password"

            TextInput(
                label = "Password",
                value = TextFieldValue(password),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Password",
                            password
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val databaseName = database.name

            val jdbcConnectionString = if (database.relationships?.password?.attributes?.password != null) {
                "jdbc:mysql://$username:$password@$endpoint/$databaseName"
            } else {
                "Unknown JDBC Connection String"
            }

            TextInput(
                label = "JDBC Connection String",
                value = TextFieldValue(jdbcConnectionString),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database JDBC Connection String",
                            jdbcConnectionString
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.setDatabaseToShowDetails(null)
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        viewModel.rotateDatabasePassword(
                            context = context,
                            databaseId = database.id,
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
                                        text = "Database password rotated successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.ERROR,
                    enabled = !state.isLoading
                ) {
                    Text("Rotate Password")
                }
            }
        }
    }

    Popup(
        showPopup = state.databaseToDelete != null,
        onDismissRequest = {
            viewModel.setDatabaseToDelete(null)
        },
        scrollable = true
    ) {
        val database = state.databases.find { it.attributes.id == state.databaseToDelete }?.attributes ?: return@Popup

        val databaseName = database.name

        Text(
            text = "Confirm database deletion",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )

        Column(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val warningString = buildAnnotatedString {
                append("Deleting a database is a permanent action, it cannot be undone. This will permanently delete the ")

                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(databaseName)
                }

                append(" database and remove all associated data")
            }

            Text(
                text = warningString
            )

            TextInput(
                label = "Confirm Database Name",
                value = state.confirmDatabaseNameValue,
                onValueChange = {
                    viewModel.setConfirmDeleteDatabaseNameValue(it)
                },
                modifier = Modifier.fillMaxWidth(),
                description = "Enter the database name to confirm deletion"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.setDatabaseToDelete(null)
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Cancel")
                }

                val validNames = listOf(
                    databaseName,
                    databaseName.split("_").drop(1).joinToString("_")
                )

                Button(
                    onClick = {
                        viewModel.deleteDatabase(
                            context = context,
                            databaseId = database.id,
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
                                        text = "Database Deleted successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.ERROR,
                    enabled = state.confirmDatabaseNameValue.text in validNames && !state.isLoading
                ) {
                    Text("Delete Database")
                }
            }
        }
    }

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
            val databaseLimit = state.server?.attributes?.featureLimits?.databases ?: 0

            Text(
                text = "$allocatedDatabases of $databaseLimit databases have been allocated to this server",
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    viewModel.showCreateDatabasePopup()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "New Database"
                )
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
                    text = "It looks like you have no databases"
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
                    onShowDatabaseDetails = { databaseId ->
                        viewModel.setDatabaseToShowDetails(databaseId)
                    },
                    onDeleteDatabase = { databaseId ->
                        viewModel.setDatabaseToDelete(databaseId)
                    }
                )
            }
        }
    }
}