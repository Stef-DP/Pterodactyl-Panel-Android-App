package com.stefdp.pterodactylpanel.screens.application.location

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.screens.ApplicationLocationScreen
import com.stefdp.pterodactylpanel.screens.ApplicationLocationsScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen

@Composable
fun ApplicationLocationScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    locationId: Long,
    viewModel: ApplicationLocationViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    fun reload(isRefresh: Boolean = false) {
        viewModel.init(
            context = context,
            locationId = locationId,
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
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(locationId) {
        reload()
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            reload(isRefresh = true)
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        val refreshScrollState = rememberScrollableState { 0f }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .scrollable(
                    state = refreshScrollState,
                    orientation = Orientation.Vertical
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Container(
                title = {
                    Text(
                        text = "Location Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    label = "Short Code",
                    value = state.shortCodeInput,
                    onValueChange = {
                        viewModel.setShortCodeInput(it)
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                TextInput(
                    label = "Description",
                    value = state.descriptionInput,
                    onValueChange = {
                        viewModel.setDescriptionInput(it)
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.deleteLocation(
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
                                    navController.navigate(ApplicationLocationsScreen) {
                                        popUpTo<ApplicationLocationScreen> { inclusive = true }
                                    }
                                }
                            )
                        },
                        icon = painterResource(R.drawable.delete),
                        iconColor = MaterialTheme.colorScheme.error,
                        iconContentDescription = "Delete Location",
                        enabled = !state.isLoading,
                        border = true,
                        borderColor = MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = {
                            viewModel.updateLocation(
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
                                            text = "Location updated successfully",
                                        )
                                    }
                                }
                            )
                        },
                        buttonType = ButtonType.PRIMARY,
                        enabled = !state.isLoading
                    ) {
                        Text("Save")
                    }
                }
            }

            Container(
                title = {
                    Text(
                        text = "Nodes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                val tableIdWidth = 50.dp
                val tableNameWidth = 250.dp
                val tableFQDNWidth = 200.dp
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
                        name = "name",
                        width = tableNameWidth,
                    ) {
                        Text(
                            text = "Name",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "FQDN",
                        width = tableFQDNWidth,
                    ) {
                        Text(
                            text = "FQDN",
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

                val rows: List<TableRowData>? = state.location?.attributes?.relationships?.nodes?.data?.map { (type, node) ->
                    val serverCount = state.location?.attributes?.relationships?.servers?.data
                        ?.filter { it.attributes.node == node.id }
                        ?.size ?: 0

                    TableRowData(
                        id = node.id.toString(),
                        cells = listOf(
                            TableCellData(
                                width = tableIdWidth
                            ) {
                                Text(
                                    text = node.id.toString(),
                                )
                            },

                            TableCellData(
                                width = tableNameWidth
                            ) {
                                Text(
                                    text = node.name,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(
                                                    ApplicationNodeScreen(
                                                        node.id
                                                    )
                                                )
                                            }
                                        )
                                )
                            },

                            TableCellData(
                                width = tableFQDNWidth
                            ) {
                                Text(
                                    text = node.fqdn,
                                )
                            },

                            TableCellData(
                                width = tableServersWidth
                            ) {
                                Text(
                                    text = serverCount.toString(),
                                )
                            },
                        )
                    )
                }

                Table(
                    loading = state.isLoading,
                    headers = headers,
                    rows = rows ?: emptyList(),
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