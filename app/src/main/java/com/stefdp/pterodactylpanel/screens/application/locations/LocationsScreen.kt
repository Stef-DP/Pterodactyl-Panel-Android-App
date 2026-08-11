package com.stefdp.pterodactylpanel.screens.application.locations

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.requests.ListLocationsQuerySort
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.screens.ApplicationLocationScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.locations.popups.CreateLocationPopup
import com.stefdp.pterodactylpanel.utils.hasPermission
import kotlinx.coroutines.launch

@Composable
fun ApplicationLocationsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ApplicationLocationsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    fun updateData(
        filterShort: String? = null,
        filterLong: String? = null,
        sort: ListLocationsQuerySort? = null,
        isRefresh: Boolean = false,
    ) {
        viewModel.updateData(
            context = context,
            isRefresh = isRefresh,
            filterShort = filterShort,
            filterLong = filterLong,
            sort = sort,
        )
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(state.page) {
        updateData()
        scrollState.animateScrollTo(0)
    }

    CreateLocationPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                updateData(
                    isRefresh = true
                )
            }
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                            text = "Locations",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "All locations that nodes can be assigned to for easier categorization",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.showCreateLocationPopup()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.locations != null
                    ) {
                        Text(
                            text = "Create New"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                val tableIdWidth = 50.dp
                val tableShortCodeWidth = 200.dp
                val tableDescriptionWidth = 250.dp
                val tableNodesWidth = 100.dp
                val tableServersWidth = 100.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        name = "id",
                        width = tableIdWidth,
                    ) {
                        Text(
                            text = "ID",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "shortCode",
                        width = tableShortCodeWidth,
                    ) {
                        Text(
                            text = "Short Code",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "description",
                        width = tableDescriptionWidth,
                    ) {
                        Text(
                            text = "Description",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "nodes",
                        width = tableNodesWidth,
                    ) {
                        Text(
                            text = "Nodes",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "servers",
                        width = tableServersWidth,
                    ) {
                        Text(
                            text = "Servers",
                            fontWeight = FontWeight.Bold
                        )
                    },
                )

                val rows: List<TableRowData>? = state.locations?.map { (type, location) ->
                    TableRowData(
                        cells = listOf(
                            TableCellData(
                                width = tableIdWidth
                            ) {
                                Text(
                                    text = location.id.toString(),
                                )
                            },

                            TableCellData(
                                width = tableShortCodeWidth
                            ) {
                                Text(
                                    text = location.short,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(ApplicationLocationScreen(location.id))
                                            }
                                        )
                                )
                            },

                            TableCellData(
                                width = tableDescriptionWidth
                            ) {
                                Text(
                                    text = location.long ?: "",
                                )
                            },

                            TableCellData(
                                width = tableNodesWidth
                            ) {
                                Text(
                                    text = (location.relationships?.nodes?.data?.size ?: 0).toString(),
                                )
                            },

                            TableCellData(
                                width = tableServersWidth
                            ) {
                                Text(
                                    text = (location.relationships?.servers?.data?.size ?: 0).toString(),
                                )
                            },
                        )
                    )
                }

                Table(
                    loading = state.locations == null,
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
                enabled = state.locations != null && !state.locations.isNullOrEmpty(),
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