package com.stefdp.pterodactylpanel.screens.application.servers

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQuerySort
import com.stefdp.pterodactylpanel.network.client.models.Server
import com.stefdp.pterodactylpanel.screens.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.ApplicationServerScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUserScreen
import com.stefdp.pterodactylpanel.screens.ClientServerScreen
import com.stefdp.pterodactylpanel.screens.application.servers.popups.CreateServerPopup
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.ui.theme.Yellow
import com.stefdp.pterodactylpanel.utils.shimmerable

@Composable
fun ApplicationServersScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ApplicationServersViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    fun updateData(
        filterUuid: String? = null,
        filterUuidShort: String? = null,
        filterName: String? = null,
        filterImage: String? = null,
        filterExternalId: String? = null,
        sort: ListServersQuerySort? = null,
        isRefresh: Boolean = false
    ) {
        viewModel.updateData(
            context = context,
            filterUuid = filterUuid,
            filterUuidShort = filterUuidShort,
            filterName = filterName,
            filterImage = filterImage,
            filterExternalId = filterExternalId,
            sort = sort,
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(state.page) {
        updateData()
    }

    CreateServerPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
    )

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            updateData(
                isRefresh = true
            )
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Servers",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "All servers available on the system",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.showCreateServerPopup()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.servers != null
                    ) {
                        Text(
                            text = "Create New"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                val tableNameWidth = 230.dp
                val tableUuidWidth = 350.dp
                val tableOwnerWidth = 200.dp
                val tableNodeWidth = 160.dp
                val tableConnectionWidth = 230.dp
                val tableStatusWidth = 190.dp
                val tableOpenWidth = 80.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        name = "server_name",
                        width = tableNameWidth
                    ) {
                        Text(
                            text = "Server Name",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "uuid",
                        width = tableUuidWidth
                    ) {
                        Text(
                            text = "UUID",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "owner",
                        width = tableOwnerWidth
                    ) {
                        Text(
                            text = "Owner",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "node",
                        width = tableNodeWidth
                    ) {
                        Text(
                            text = "Node",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "Connection",
                        width = tableConnectionWidth
                    ) {
                        Text(
                            text = "Connection",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "status",
                        width = tableStatusWidth
                    ) {
                        Text(
                            text = "Status",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "open",
                        width = tableOpenWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Open",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                val rows: List<TableRowData>? = state.servers?.map { (_, server) ->
                    TableRowData(
                        id = server.id.toString(),
                        cells = listOf(
                            TableCellData(
                                width = tableNameWidth
                            ) {
                                Text(
                                    text = server.name,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable(
                                        enabled = true,
                                        onClick = {
                                            navController.navigate(ApplicationServerScreen(server.id))
                                        }
                                    )
                                )
                            },

                            TableCellData(
                                width = tableUuidWidth
                            ) {
                                CodeText(
                                    text = "`${server.uuid}`"
                                )
                            },

                            TableCellData(
                                width = tableOwnerWidth
                            ) {
                                val user = server.relationships?.user?.attributes

                                Text(
                                    text = user?.username ?: "Loading...",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .shimmerable(
                                            enabled = user == null
                                        )
                                        .clickable(
                                            enabled = user != null,
                                            onClick = {
                                                navController.navigate(ApplicationUserScreen(user!!.id))
                                            }
                                        )
                                )
                            },

                            TableCellData(
                                width = tableNodeWidth
                            ) {
                                val node = server.relationships?.node?.attributes

                                Text(
                                    text = node?.name ?: "Loading...",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .shimmerable(
                                            enabled = node == null
                                        )
                                        .clickable(
                                            enabled = node != null,
                                            onClick = {
                                                navController.navigate(ApplicationNodeScreen(node!!.id))
                                            }
                                        )
                                )
                            },

                            TableCellData(
                                width = tableConnectionWidth
                            ) {
                                val allocations = server.relationships?.allocations?.data
                                val defaultAllocation = allocations?.find { it.attributes.id == server.allocation }?.attributes

                                CodeText(
                                    text = "`${defaultAllocation?.ip ?: "127.0.0.1"}:${defaultAllocation?.port ?: "12345"}`",
                                    modifier = Modifier.shimmerable(
                                        enabled = defaultAllocation == null
                                    )
                                )
                            },

                            TableCellData(
                                width = tableStatusWidth
                            ) {
                                val errorCategories = listOf(
                                    ApplicationServer.Attributes.Status.SUSPENDED,
                                    ApplicationServer.Attributes.Status.INSTALL_FAILED,
                                    ApplicationServer.Attributes.Status.REINSTALL_FAILED
                                )

                                val warningCategories = listOf(
                                    ApplicationServer.Attributes.Status.INSTALLING,
                                    ApplicationServer.Attributes.Status.RESTORING_BACKUP,
                                )

                                val pillColor = when {
                                    server.suspended || server.status in errorCategories -> MaterialTheme.colorScheme.error
                                    server.status in warningCategories -> Yellow
                                    else -> Green
                                }

                                val pillText = when {
                                    server.suspended || server.status == ApplicationServer.Attributes.Status.SUSPENDED -> "Suspended"
                                    server.status == ApplicationServer.Attributes.Status.INSTALLING -> "Installing"
                                    server.status == ApplicationServer.Attributes.Status.RESTORING_BACKUP -> "Restoring Backup"
                                    server.status == ApplicationServer.Attributes.Status.INSTALL_FAILED -> "Install Failed"
                                    server.status == ApplicationServer.Attributes.Status.REINSTALL_FAILED -> "Reinstall Failed"
                                    else -> "Active"
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                        .background(pillColor)
                                        .padding(
                                            top = 4.dp,
                                            bottom = 4.dp,
                                            start = 8.dp,
                                            end = 8.dp
                                        )
                                ) {
                                    Text(
                                        text = pillText,
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                            },

                            TableCellData(
                                width = tableOpenWidth,
                                arrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    icon = painterResource(R.drawable.build_fill),
                                    iconContentDescription = "Open Server",
                                    border = true,
                                    borderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    onClick = {
                                        navController.navigate(ClientServerScreen(server.identifier))
                                    }
                                )
                            },
                        )
                    )
                }

                Table(
                    headers = headers,
                    rows = rows ?: emptyList(),
                    loading = state.servers == null,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        ),
                    lazy = false
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Pager(
                    currentPage = state.page,
                    totalPages = state.pagination?.totalPages ?: 1,
                    enabled = state.servers != null && !state.servers.isNullOrEmpty(),
                    onFirstPageClick = {
                        viewModel.setPage(1)
                    },
                    onPreviousPageClick = {
                        viewModel.setPage(state.page - 1)
                    },
                    onCustomPageInput = { page ->
                        viewModel.setPage(page)
                    },
                    onNextPageClick = {
                        viewModel.setPage(state.page + 1)
                    },
                    onLastPageClick = {
                        viewModel.setPage(state.pagination?.totalPages ?: 1)
                    },
                )
            }
        }
    }
}