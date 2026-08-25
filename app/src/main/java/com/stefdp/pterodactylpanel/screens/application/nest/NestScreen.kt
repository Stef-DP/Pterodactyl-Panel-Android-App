package com.stefdp.pterodactylpanel.screens.application.nest

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.screens.ApplicationNestEggScreen
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun ApplicationNestScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    nestId: Long,
    viewModel: ApplicationNestViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    fun reload(isRefresh: Boolean = false) {
        viewModel.init(
            context = context,
            nestId = nestId,
            isRefresh = isRefresh,
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
        )
    }

    LaunchedEffect(nestId) {
        reload()
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            reload(isRefresh = true)
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .verticalScrollWithScrollbar(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        text = state.nest?.attributes?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .shimmerable(
                                enabled = state.nest == null
                            )
                    )

                    Text(
                        text = state.nest?.attributes?.description ?: "Loading Description...",
                        modifier = Modifier
                            .shimmerable(
                                enabled = state.nest == null,
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    TextInput(
                        value = TextFieldValue(state.nest?.attributes?.name ?: ""),
                        onValueChange = {},
                        label = "Name",
                        description = "This should be a descriptive category name that encompasses all of the options within the service",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TextInput(
                        value = TextFieldValue(state.nest?.attributes?.description ?: ""),
                        onValueChange = {},
                        label = "Description",
                        readOnly = true,
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Identifiers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    TextInput(
                        value = TextFieldValue(state.nest?.attributes?.id?.toString() ?: ""),
                        onValueChange = {},
                        label = "Nest ID",
                        description = "A unique ID used for identification of this nest internally and through the API",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TextInput(
                        value = TextFieldValue(state.nest?.attributes?.author ?: ""),
                        onValueChange = {},
                        label = "Author",
                        description = "The author of this service option. Please direct questions and issues to them unless this is an official option authored by `support@pterodactyl.io`",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TextInput(
                        value = TextFieldValue(state.nest?.attributes?.uuid ?: ""),
                        onValueChange = {},
                        label = "UUID",
                        description = "A UUID that all servers using this option are assigned for identification purposes",
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Container(
                    title = {
                        Text(
                            text = "Nest Eggs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    modifier = Modifier.heightIn(
                        max = 400.dp
                    )
                ) {
                    val tableIdWidth = 100.dp
                    val tableNameWidth = 180.dp
                    val tableDescriptionWidth = 250.dp
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
                            name = "servers",
                            width = tableServersWidth,
                            arrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Servers",
                                fontWeight = FontWeight.Bold,
                            )
                        },
                    )

                    val rows: List<TableRowData>? = state.nest?.attributes?.relationships?.eggs?.data?.map { (_, egg) ->
                        TableRowData(
                            id = egg.id.toString(),
                            cells = listOf(
                                TableCellData(
                                    width = tableIdWidth,
                                    arrangement = Arrangement.Center
                                ) {
                                    CodeText(
                                        text = "`${egg.id}`",
                                    )
                                },

                                TableCellData(
                                    width = tableNameWidth,
                                ) {
                                    Text(
                                        text = egg.name,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.clickable(
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(
                                                    ApplicationNestEggScreen(
                                                        nestId = egg.nest,
                                                        eggId = egg.id
                                                    )
                                                )
                                            }
                                        )
                                    )
                                },

                                TableCellData(
                                    width = tableDescriptionWidth,
                                ) {
                                    Text(
                                        text = egg.description,
                                    )
                                },

                                TableCellData(
                                    width = tableServersWidth,
                                    arrangement = Arrangement.Center
                                ) {
                                    CodeText(
                                        text = "`${egg.relationships?.servers?.data?.size ?: 0}`",
                                    )
                                },
                            )
                        )
                    }

                    Table(
                        headers = headers,
                        rows = rows ?: emptyList(),
                        loading = state.nest == null,
                        modifier = Modifier
                            .weight(1f)
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
    }
}