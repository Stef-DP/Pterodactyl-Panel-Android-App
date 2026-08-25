package com.stefdp.pterodactylpanel.screens.application.nests

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.screens.ApplicationNestScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen

@Composable
fun ApplicationNestsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ApplicationNestsViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    fun updateData(isRefresh: Boolean = false) {
        viewModel.updateData(
            context = context,
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(state.page) {
        updateData()
    }

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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Nests",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "All nests currently available on this system",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    var isNotificationExpanded by rememberSaveable {
                        mutableStateOf(false)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .background(MaterialTheme.colorScheme.error)
                            .padding(12.dp)
                            .animateContentSize()
                            .height(
                                if (isNotificationExpanded) {
                                    Dp.Unspecified
                                } else {
                                    48.dp
                                }
                            ),
                        verticalAlignment = Alignment.Top
                    ) {
                        CodeText(
                            text = "Eggs are a powerful feature of Pterodactyl Panel that allow for extreme flexibility and configuration. Please note that while powerful, modifying an egg wrongly can very easily brick your servers and cause more problems. Please avoid editing default eggs — those provided by `support@pterodactyl.io` — unless you are absolutely sure of what you are doing",
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis
                        )

                        val rotationAngle by animateFloatAsState(
                            targetValue = if (isNotificationExpanded) 0f else 180f,
                            animationSpec = tween(durationMillis = 300),
                            label = "IconRotation"
                        )

                        Icon(
                            painter = painterResource(R.drawable.keyboard_arrow_up),
                            contentDescription = "Expand/Collapse",
                            modifier = Modifier
                                .rotate(rotationAngle)
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        isNotificationExpanded = !isNotificationExpanded
                                    }
                                )
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    val tableIdWidth = 100.dp
                    val tableNameWidth = 180.dp
                    val tableDescriptionWidth = 220.dp
                    val tableEggsWidth = 100.dp
                    val tableServersWidth = 100.dp

                    val headers: List<TableHeaderData> = listOf(
                        TableHeaderData(
                            name = "id",
                            width = tableIdWidth,
                            arrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "ID",
                                fontWeight = FontWeight.Bold,
                            )
                        },

                        TableHeaderData(
                            name = "name",
                            width = tableNameWidth,
                        ) {
                            Text(
                                text = "Name",
                                fontWeight = FontWeight.Bold,
                            )
                        },

                        TableHeaderData(
                            name = "description",
                            width = tableDescriptionWidth,
                        ) {
                            Text(
                                text = "Description",
                                fontWeight = FontWeight.Bold,
                            )
                        },

                        TableHeaderData(
                            name = "eggs",
                            width = tableEggsWidth,
                            arrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Eggs",
                                fontWeight = FontWeight.Bold,
                            )
                        },

                        TableHeaderData(
                            name = "servers",
                            width = tableServersWidth,
                            arrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Servers",
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    )

                    val rows: List<TableRowData>? = state.nests?.map { (_, nest) ->
                        TableRowData(
                            id = nest.id.toString(),
                            cells = listOf(
                                TableCellData(
                                    width = tableIdWidth,
                                    arrangement = Arrangement.Center
                                ) {
                                    CodeText(
                                        text = "`${nest.id}`"
                                    )
                                },

                                TableCellData(
                                    width = tableNameWidth
                                ) {
                                    Text(
                                        text = nest.name,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable(
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(ApplicationNestScreen(nest.id))
                                            }
                                        )
                                    )
                                },

                                TableCellData(
                                    width = tableDescriptionWidth
                                ) {
                                    Text(
                                        text = nest.description ?: ""
                                    )
                                },

                                TableCellData(
                                    width = tableEggsWidth,
                                    arrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = (nest.relationships?.eggs?.data?.size ?: 0).toString()
                                    )
                                },

                                TableCellData(
                                    width = tableServersWidth,
                                    arrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = (nest.relationships?.servers?.data?.size ?: 0).toString()
                                    )
                                }
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = state.nests == null,
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
                        enabled = !state.nests.isNullOrEmpty(),
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
}