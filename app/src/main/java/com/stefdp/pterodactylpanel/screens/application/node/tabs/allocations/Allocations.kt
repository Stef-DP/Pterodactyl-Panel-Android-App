package com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Checkbox
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.screens.ApplicationServerScreen
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.popups.BulkDeleteAllocationsPopup
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.popups.CreateAllocationsPopup
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.popups.DeleteAllocationPopup

@Composable
fun AllocationsTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNodeAllocationsTabViewModel = viewModel(),
    node: ApplicationNode?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    fun updateData() {
        viewModel.updateAllocations(
            context = context,
            onSuccess = {},
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

    LaunchedEffect(node?.attributes?.id, refreshIndex) {
        if (node == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad && state.allocations.isEmpty()) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(node)
        updateData()
    }

    LaunchedEffect(state.page) {
        updateData()
    }

    DeleteAllocationPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    BulkDeleteAllocationsPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    CreateAllocationsPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                viewModel.showCreateAllocationsPopup()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(
                text = "Create Allocations"
            )
        }

        AnimatedVisibility(
            visible = state.selectedAllocations.isNotEmpty()
        ) {
            Button(
                onClick = {
                    viewModel.showBulkDeletePopup()
                },
                buttonType = ButtonType.ERROR,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(
                    text = "Bulk Delete"
                )
            }
        }

        val tableCheckboxWidth = 50.dp
        val tableIPWidth = 170.dp
        val tableIpAliasWidth = 300.dp
        val tablePortWidth = 80.dp
        val tableAssignedToWidth = 230.dp

        val headers: List<TableHeaderData> = listOf(
            TableHeaderData(
                name = "checkbox",
                width = tableCheckboxWidth,
                arrangement = Arrangement.Center
            ) {
                val selectableAllocationIds = remember(state.allocations) {
                    state.allocations
                        .filter { !it.attributes.assigned }
                        .map { it.attributes.id }
                        .toSet()
                }

                val allSelected = selectableAllocationIds.isNotEmpty() &&
                        selectableAllocationIds.all { it in state.selectedAllocations }

                Checkbox(
                    checked = allSelected,
                    enabled = selectableAllocationIds.isNotEmpty(),
                    onToggle = {
                        viewModel.setSelectedAllocations(
                            if (allSelected) emptySet() else selectableAllocationIds
                        )
                    }
                )
            },

            TableHeaderData(
                name = "ip_address",
                width = tableIPWidth
            ) {
                Text(
                    text = "IP Address",
                    fontWeight = FontWeight.Bold
                )
            },

            TableHeaderData(
                name = "ip_alias",
                width = tableIpAliasWidth
            ) {
                Text(
                    text = "IP Alias",
                    fontWeight = FontWeight.Bold
                )
            },

            TableHeaderData(
                name = "port",
                width = tablePortWidth
            ) {
                Text(
                    text = "Port",
                    fontWeight = FontWeight.Bold
                )
            },

            TableHeaderData(
                name = "assigned_to",
                width = tableAssignedToWidth
            ) {
                Text(
                    text = "Assigned To",
                    fontWeight = FontWeight.Bold
                )
            }
        )

        val rows: List<TableRowData> = state.allocations.map { (_, allocation) ->
            TableRowData(
                id = allocation.id.toString(),
                cells = listOf(
                    TableCellData(
                        width = tableCheckboxWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = allocation.id in state.selectedAllocations,
                            enabled = !allocation.assigned,
                            onToggle = {
                                val isSelected = allocation.id in state.selectedAllocations

                                if (isSelected) {
                                    viewModel.setSelectedAllocations(
                                        state.selectedAllocations - allocation.id
                                    )
                                } else {
                                    viewModel.setSelectedAllocations(
                                        state.selectedAllocations + allocation.id
                                    )
                                }
                            }
                        )
                    },

                    TableCellData(
                        width = tableIPWidth
                    ) {
                        Text(
                            text = allocation.ip
                        )
                    },

                    TableCellData(
                        width = tableIpAliasWidth
                    ) {
                        Text(
                            text = allocation.alias ?: ""
                        )
                    },

                    TableCellData(
                        width = tablePortWidth
                    ) {
                        Text(
                            text = allocation.port.toString()
                        )
                    },

                    TableCellData(
                        width = tableAssignedToWidth
                    ) {
                        if (allocation.assigned && allocation.relationships?.server != null) {
                            Text(
                                text = allocation.relationships.server.attributes.name,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable(
                                    enabled = true,
                                    onClick = {
                                        navController.navigate(
                                            ApplicationServerScreen(
                                                allocation.relationships.server.attributes.id
                                            )
                                        )
                                    }
                                )
                            )
                        } else {
                            IconButton(
                                icon = painterResource(R.drawable.delete),
                                iconContentDescription = "Delete Allocation",
                                iconColor = MaterialTheme.colorScheme.error,
                                borderColor = MaterialTheme.colorScheme.error,
                                border = true,
                                onClick = {
                                    viewModel.setAllocationToDelete(allocation.id)
                                }
                            )
                        }
                    },
                )
            )
        }

        Table(
            headers = headers,
            rows = rows,
            loading = state.isLoading,
            modifier = Modifier
                .padding(
                    vertical = 8.dp
                )
                .weight(1f)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                ),
            lazy = false
        )

        Pager(
            currentPage = state.page,
            totalPages = state.pagination?.totalPages ?: 1,
            enabled = !state.isLoading,
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