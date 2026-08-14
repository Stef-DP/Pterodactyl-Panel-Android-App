package com.stefdp.pterodactylpanel.screens.application.nodes

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQuerySort
import com.stefdp.pterodactylpanel.screens.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.nodes.popups.CreateNodePopup
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.ui.theme.Yellow
import nl.jacobras.humanreadable.HumanReadable

@Composable
fun ApplicationNodesScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ApplicationNodesViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    fun updateData(
        filterUuid: String? = null,
        filterName: String? = null,
        filterFQDN: String? = null,
        filterDaemonTokenId: String? = null,
        sort: ListNodesQuerySort? = null,
        isRefresh: Boolean = false
    ) {
        viewModel.updateData(
            context = context,
            filterUuid = filterUuid,
            filterName = filterName,
            filterFQDN = filterFQDN,
            filterDaemonTokenId = filterDaemonTokenId,
            sort = sort,
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(state.page) {
        updateData()
    }

    CreateNodePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
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
                            text = "Nodes",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "All nodes available on the system",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.showCreateNodePopup()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.nodes != null && !state.locationsLoading
                    ) {
                        Text(
                            text = "Create New"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                val tableStatusWidth = 80.dp
                val tableNameWidth = 200.dp
                val tableLocationWidth = 200.dp
                val tableMemoryWidth = 130.dp
                val tableDiskWidth = 130.dp
                val tableServersWidth = 100.dp
                val tableSSLWidth = 70.dp
                val tablePublicWidth = 75.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        name = "status",
                        width = tableStatusWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Status",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "name",
                        width = tableNameWidth
                    ) {
                        Text(
                            text = "Name",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "location",
                        width = tableLocationWidth
                    ) {
                        Text(
                            text = "Location",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "memory",
                        width = tableMemoryWidth
                    ) {
                        Text(
                            text = "Memory",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "disk",
                        width = tableDiskWidth
                    ) {
                        Text(
                            text = "Disk",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "servers",
                        width = tableServersWidth
                    ) {
                        Text(
                            text = "Servers",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "ssl",
                        width = tableSSLWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SSL",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "public",
                        width = tablePublicWidth
                    ) {
                        Text(
                            text = "Public",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                val rows: List<TableRowData>? = state.nodes?.map { (_, node) ->
                    TableRowData(
                        cells = listOf(
                            TableCellData(
                                width = tableStatusWidth,
                                arrangement = Arrangement.Center
                            ) {
                                val isNodeOnline = state.nodesStatus[node.id]

                                if (isNodeOnline == null) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                    )

                                    return@TableCellData
                                }

                                val icon = when (isNodeOnline) {
                                    true -> painterResource(id = R.drawable.ecg_heart_fill)
                                    false -> painterResource(id = R.drawable.favorite)
                                }

                                val iconColor = when (isNodeOnline) {
                                    true -> Green
                                    false -> MaterialTheme.colorScheme.error
                                }

                                val iconContentDescription = when (isNodeOnline) {
                                    true -> "Node is online"
                                    false -> "Node is offline"
                                }

                                Icon(
                                    painter = icon,
                                    contentDescription = iconContentDescription,
                                    tint = iconColor
                                )
                            },

                            TableCellData(
                                width = tableNameWidth,
                            ) {
                                if (node.maintenanceMode) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                            .background(Yellow)
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.build_fill),
                                            contentDescription = "Maintenance Mode",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.width(4.dp)
                                    )
                                }

                                Text(
                                    text = node.name,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(ApplicationNodeScreen(node.id))
                                            }
                                        )
                                )
                            },

                            TableCellData(
                                width = tableLocationWidth,
                            ) {
                                Text(
                                    text = node.relationships?.location?.attributes?.short ?: "Unknown"
                                )
                            },

                            TableCellData(
                                width = tableMemoryWidth,
                            ) {
                                val bytes = node.memory * 1024L * 1024L

                                Text(
                                    text = HumanReadable.fileSize(
                                        bytes = bytes,
                                        decimals = 2
                                    )
                                )
                            },

                            TableCellData(
                                width = tableDiskWidth,
                            ) {
                                val bytes = node.disk * 1024L * 1024L

                                Text(
                                    text = HumanReadable.fileSize(
                                        bytes = bytes,
                                        decimals = 2
                                    )
                                )
                            },

                            TableCellData(
                                width = tableServersWidth,
                            ) {
                                Text(
                                    text = node.relationships?.servers?.data?.size?.toString() ?: "0"
                                )
                            },

                            TableCellData(
                                width = tableSSLWidth,
                                arrangement = Arrangement.Center
                            ) {
                                val icon = when (node.scheme) {
                                    ApplicationNode.Attributes.Scheme.HTTPS -> painterResource(R.drawable.lock)
                                    ApplicationNode.Attributes.Scheme.HTTP -> painterResource(R.drawable.lock_open)
                                }

                                val iconColor = when (node.scheme) {
                                    ApplicationNode.Attributes.Scheme.HTTPS -> Green
                                    ApplicationNode.Attributes.Scheme.HTTP -> MaterialTheme.colorScheme.error
                                }

                                val iconContentDescription = when (node.scheme) {
                                    ApplicationNode.Attributes.Scheme.HTTPS -> "Node uses HTTPS"
                                    ApplicationNode.Attributes.Scheme.HTTP -> "Node uses HTTP"
                                }

                                Icon(
                                    painter = icon,
                                    contentDescription = iconContentDescription,
                                    tint = iconColor
                                )
                            },

                            TableCellData(
                                width = tablePublicWidth,
                                arrangement = Arrangement.Center
                            ) {
                                val icon = when (node.public) {
                                    true -> painterResource(R.drawable.visibility)
                                    false -> painterResource(R.drawable.visibility_off)
                                }

                                val iconContentDescription = when (node.public) {
                                    true -> "Node is public"
                                    false -> "Node is private"
                                }

                                Icon(
                                    painter = icon,
                                    contentDescription = iconContentDescription,
                                )
                            }
                        )
                    )
                }

                Table(
                    loading = state.nodes == null,
                    headers = headers,
                    rows = rows ?: emptyList(),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        )
                )
            }

            Pager(
                currentPage = state.page,
                totalPages = state.pagination?.totalPages ?: 1,
                enabled = !state.nodes.isNullOrEmpty(),
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
                }
            )
        }
    }
}