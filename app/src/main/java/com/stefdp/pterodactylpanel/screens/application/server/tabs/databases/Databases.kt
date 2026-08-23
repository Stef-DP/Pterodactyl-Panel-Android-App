package com.stefdp.pterodactylpanel.screens.application.server.tabs.databases

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.screens.ClientServerScreen
import com.stefdp.pterodactylpanel.screens.application.server.tabs.databases.popups.DeleteDatabasePopup

@Composable
fun DatabasesTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationServerDatabasesTabViewModel = viewModel(),
    server: ApplicationServer?,
    refreshIndex: Int,
    reload: (
        isRefresh: Boolean,
        onReloadFinish: () -> Unit,
        increaseRefreshIndex: Boolean,
        onError: (String) -> Unit
    ) -> Unit
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

        viewModel.init(server)
    }

    DeleteDatabasePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
        reload = reload
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Database passwords can be viewed when ")

                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "CLIENT_DATABASES",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline
                                )
                            ),
                            linkInteractionListener = {
                                if (server == null) return@Clickable

                                navController.navigate(
                                    ClientServerScreen(
                                        serverId = server.attributes.identifier,
                                        switchToDatabases = true
                                    )
                                )
                            }
                        )
                    ) {
                        append("visiting this server")
                    }

                    append(" on the front-end")
                }
            )
        }

        Container(
            title = {
                Text(
                    text = "Active Databases",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            val tableDatabaseWidth = 180.dp
            val tableUsernameWidth = 190.dp
            val tableConnectionsFromWidth = 200.dp
            val tableHostWidth = 230.dp
            val tableMaxConnectionsWidth = 200.dp
            val tableActionsWidth = 120.dp

            val headers: List<TableHeaderData> = listOf(
                TableHeaderData(
                    name = "database",
                    width = tableDatabaseWidth
                ) {
                    Text(
                        text = "Database",
                        fontWeight = FontWeight.Bold
                    )
                },

                TableHeaderData(
                    name = "username",
                    width = tableUsernameWidth
                ) {
                    Text(
                        text = "Username",
                        fontWeight = FontWeight.Bold
                    )
                },

                TableHeaderData(
                    name = "connections_from",
                    width = tableConnectionsFromWidth
                ) {
                    Text(
                        text = "Connections From",
                        fontWeight = FontWeight.Bold
                    )
                },

                TableHeaderData(
                    name = "host",
                    width = tableHostWidth
                ) {
                    Text(
                        text = "Host",
                        fontWeight = FontWeight.Bold
                    )
                },

                TableHeaderData(
                    name = "max_connections",
                    width = tableMaxConnectionsWidth
                ) {
                    Text(
                        text = "Max Connections",
                        fontWeight = FontWeight.Bold
                    )
                },

                TableHeaderData(
                    name = "actions",
                    width = tableActionsWidth,
                    arrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Actions",
                        fontWeight = FontWeight.Bold
                    )
                }
            )

            val rows = state.databases.map { (_, database) ->
                TableRowData(
                    id = database.id.toString(),
                    cells = listOf(
                        TableCellData(
                            width = tableDatabaseWidth
                        ) {
                            Text(
                                text = database.database
                            )
                        },

                        TableCellData(
                            width = tableUsernameWidth
                        ) {
                            Text(
                                text = database.username
                            )
                        },

                        TableCellData(
                            width = tableConnectionsFromWidth
                        ) {
                            Text(
                                text = database.remote
                            )
                        },

                        TableCellData(
                            width = tableHostWidth
                        ) {
                            CodeText(
                                text = "`${database.relationships?.host?.attributes?.host}:${database.relationships?.host?.attributes?.port}`"
                            )
                        },

                        TableCellData(
                            width = tableMaxConnectionsWidth,
                            arrangement = Arrangement.Center
                        ) {
                            Text(
                                text = database.maxConnections.toString()
                            )
                        },

                        TableCellData(
                            width = tableActionsWidth,
                            arrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterHorizontally
                            )
                        ) {
                            IconButton(
                                icon = painterResource(R.drawable.reset_settings),
                                iconContentDescription = "Reset Password",
                                iconColor = MaterialTheme.colorScheme.primaryContainer,
                                borderColor = MaterialTheme.colorScheme.primaryContainer,
                                border = true,
                                onClick = {
                                    viewModel.resetDatabasePassword(
                                        context = context,
                                        databaseId = database.id,
                                        onSuccess = {
                                            Notification.show(
                                                activity = activity,
                                                duration = 3000L
                                            ) {
                                                Text(
                                                    text = "Database password reset successfully"
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
                                }
                            )

                            IconButton(
                                icon = painterResource(R.drawable.delete),
                                iconContentDescription = "Delete Database",
                                iconColor = MaterialTheme.colorScheme.errorContainer,
                                borderColor = MaterialTheme.colorScheme.errorContainer,
                                border = true,
                                onClick = {
                                    viewModel.setDatabaseToDelete(database.id)
                                }
                            )
                        }
                    )
                )
            }

            Table(
                headers = headers,
                rows = rows,
                loading = state.isLoading,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceDim,
                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                    ),
                borderColor = MaterialTheme.colorScheme.surfaceDim
            )
        }
    }
}