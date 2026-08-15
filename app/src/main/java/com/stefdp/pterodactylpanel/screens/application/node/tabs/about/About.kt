package com.stefdp.pterodactylpanel.screens.application.node.tabs.about

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.responses.GetNodeConfigurationResponse
import com.stefdp.pterodactylpanel.ui.theme.DarkGreen
import com.stefdp.pterodactylpanel.ui.theme.DarkerGreen
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import nl.jacobras.humanreadable.HumanReadable

@Composable
fun AboutTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNodeAboutTabViewModel = viewModel(),
    node: ApplicationNode?,
    nodeConfiguration: GetNodeConfigurationResponse? = null,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(node?.attributes?.id, nodeConfiguration, refreshIndex) {
        if (node == null || nodeConfiguration == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(
            node = node,
            nodeConfiguration = nodeConfiguration,
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Container(
            title = {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                @Composable
                fun InfoRow(
                    label: String,
                    value: @Composable RowScope.() -> Unit
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            value()
                        }
                    }
                }

                InfoRow(
                    label = "Daemon Version",
                ) {
                    CodeText(
                        text = "`${state.systemData?.version ?: "N/A"}`",
                        modifier = Modifier.shimmerable(
                            enabled = state.systemData?.version == null,
                            width = 80.dp
                        )
                    )
                }

                InfoRow(
                    label = "System Information"
                ) {
                    CodeText(
                        text = "${state.systemData?.system?.os} (${state.systemData?.system?.architecture}) `${state.systemData?.system?.kernelVersion ?: "N/A"}`",
                        modifier = Modifier.shimmerable(
                            enabled =
                                state.systemData?.system?.os == null ||
                                state.systemData?.system?.architecture == null ||
                                state.systemData?.system?.kernelVersion == null,
                            width = 100.dp,
                        ),
                        textAlign = TextAlign.End
                    )
                }

                InfoRow(
                    label = "Total CPU Threads"
                ) {
                    Text(
                        text = state.systemData?.system?.cpuThreads?.toString() ?: "N/A",
                        modifier = Modifier.shimmerable(
                            enabled = state.systemData?.system?.cpuThreads == null,
                            width = 30.dp
                        )
                    )
                }
            }
        }

        Container(
            title = {
                Text(
                    text = "At a Glance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            @Composable
            fun StatDisplay(
                label: String,
                icon: Painter,
                iconContentDescription: String,
                color: Color,
                currentValue: Long = 0L,
                maxValue: Long = 0L,
                value: String? = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .background(color)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = iconContentDescription,
                        modifier = Modifier.size(40.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Text(
                            text = if (value != null) {
                                value
                            } else {
                                val currentBytes = currentValue * 1024L * 1024L
                                val maxBytes = maxValue * 1024L * 1024L

                                val formattedCurrent = HumanReadable.fileSize(
                                    bytes = currentBytes,
                                    decimals = 2
                                )

                                val formattedMax = HumanReadable.fileSize(
                                    bytes = maxBytes,
                                    decimals = 2
                                )

                                "$formattedCurrent / $formattedMax"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.shimmerable(
                                enabled = if (value != null) {
                                    false
                                } else {
                                    maxValue == 0L
                                },
                                width = 100.dp
                            )
                        )

                        if (value != null && maxValue == 0L) return

                        if (maxValue == 0L) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                trackColor = DarkerGreen,
                                color = Color.White,

                            )
                        } else {
                            val progress = if (maxValue > 0) {
                                currentValue.toFloat() / maxValue.toFloat()
                            } else {
                                0f
                            }

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                trackColor = DarkerGreen,
                                color = Color.White,
                                drawStopIndicator = {}
                            )
                        }
                    }
                }
            }

            StatDisplay(
                label = "Disk Space Allocated",
                currentValue = state.node?.attributes?.allocatedResources?.disk ?: 0L,
                maxValue = state.node?.attributes?.disk ?: 0L,
                icon = painterResource(R.drawable.folder),
                iconContentDescription = "Disk Space Icon",
                color = DarkGreen
            )

            StatDisplay(
                label = "Memory Allocated",
                currentValue = state.node?.attributes?.allocatedResources?.memory ?: 0L,
                maxValue = state.node?.attributes?.memory ?: 0L,
                icon = painterResource(R.drawable.memory_alt),
                iconContentDescription = "Memory Icon",
                color = DarkGreen
            )

            StatDisplay(
                label = "Total Servers",
                value = (state.node?.attributes?.relationships?.servers?.data?.size ?: 0L).toString(),
                icon = painterResource(R.drawable.storage),
                iconContentDescription = "Servers Icon",
                color = MaterialTheme.colorScheme.primary
            )
        }

        Container(
            title = {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Text(
                text = "Deleting a node is a irreversible action and will immediately remove this node from the panel. There must be no servers associated with this node in order to continue"
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.deleteNode(
                        context = context,
                        onSuccess = {
                            navController.popBackStack()
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
                },
                buttonType = ButtonType.ERROR,
                enabled = state.node?.attributes?.relationships?.servers?.data?.isEmpty() == true && !state.isLoading
            ) {
                Text(
                    text = "Yes, Delete This Node"
                )
            }
        }
    }
}