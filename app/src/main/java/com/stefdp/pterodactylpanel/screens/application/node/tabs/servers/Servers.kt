package com.stefdp.pterodactylpanel.screens.application.node.tabs.servers

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.screens.ApplicationServerScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUserScreen
import com.stefdp.pterodactylpanel.utils.shimmerable

@Composable
fun ServersTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNodeServersTabViewModel = viewModel(),
    node: ApplicationNode?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(node?.attributes?.id, refreshIndex) {
        if (node == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad && state.servers.isEmpty()) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(node)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val tableIdWidth = 110.dp
        val tableNameWidth = 230.dp
        val tableOwnerWidth = 200.dp
        val tableServiceWidth = 210.dp

        val headers: List<TableHeaderData> = listOf(
            TableHeaderData(
                name = "id",
                width = tableIdWidth
            ) {
                Text(
                    text = "ID",
                    fontWeight = FontWeight.Bold
                )
            },

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
                name = "owner",
                width = tableOwnerWidth
            ) {
                Text(
                    text = "Owner",
                    fontWeight = FontWeight.Bold
                )
            },

            TableHeaderData(
                name = "service",
                width = tableServiceWidth
            ) {
                Text(
                    text = "Service",
                    fontWeight = FontWeight.Bold
                )
            }
        )

        val rows: List<TableRowData> = state.servers.map { (_, server) ->
            TableRowData(
                id = server.id.toString(),
                cells = listOf(
                    TableCellData(
                        width = tableIdWidth
                    ) {
                        CodeText(
                            text = "`${server.identifier}`"
                        )
                    },

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
                        width = tableServiceWidth
                    ) {
                        val nest = server.relationships?.nest?.attributes
                        val egg = server.relationships?.egg?.attributes

                        Text(
                            text = "${nest?.name ?: "Loading..."} (${egg?.name ?: "Loading..."})",
                            modifier = Modifier.shimmerable(
                                enabled = nest == null || egg == null
                            )
                        )
                    }
                )
            )
        }

        Table(
            headers = headers,
            rows = rows,
            loading = false,
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
        )
    }
}