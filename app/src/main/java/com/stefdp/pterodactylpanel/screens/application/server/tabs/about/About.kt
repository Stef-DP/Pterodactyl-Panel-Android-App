package com.stefdp.pterodactylpanel.screens.application.server.tabs.about

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.screens.ApplicationNestEggScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNestScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUserScreen
import com.stefdp.pterodactylpanel.screens.application.servers.errorCategories
import com.stefdp.pterodactylpanel.screens.application.servers.warningCategories
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.ui.theme.Yellow
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import nl.jacobras.humanreadable.HumanReadable
import java.util.Collections.swap

@Composable
fun AboutTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationServerAboutTabViewModel = viewModel(),
    server: ApplicationServer?,
    refreshIndex: Int
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState)
    ) {
        Container(
            title = {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                val pillColor = when {
                    server?.attributes?.suspended == true || server?.attributes?.status in errorCategories -> MaterialTheme.colorScheme.error
                    server?.attributes?.status in warningCategories -> Yellow
                    else -> Green
                }

                val pillText = when {
                    server?.attributes?.suspended == true || server?.attributes?.status == ApplicationServer.Attributes.Status.SUSPENDED -> "Suspended"
                    server?.attributes?.status == ApplicationServer.Attributes.Status.INSTALLING -> "Installing"
                    server?.attributes?.status == ApplicationServer.Attributes.Status.RESTORING_BACKUP -> "Restoring Backup"
                    server?.attributes?.status == ApplicationServer.Attributes.Status.INSTALL_FAILED -> "Install Failed"
                    server?.attributes?.status == ApplicationServer.Attributes.Status.REINSTALL_FAILED -> "Reinstall Failed"
                    else -> "Active"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .shimmerable(
                            enabled = state.server == null
                        )
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
            titleArrangement = Arrangement.SpaceBetween,
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
                    label = "Internal Identifier"
                ) {
                    CodeText(
                        text = "`${state.server?.attributes?.id ?: "-1"}`",
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 40.dp
                        )
                    )
                }

                InfoRow(
                    label = "External Identifier"
                ) {
                    val identifier = state.server?.attributes?.externalId

                    CodeText(
                        text = if (identifier.isNullOrBlank()) {
                            "Not Set"
                        } else {
                            "`${identifier}`"
                        },
                        textAlign = TextAlign.End,
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 70.dp
                        )
                    )
                }

                InfoRow(
                    label = "UUID / Docker\nContainer ID"
                ) {
                    CodeText(
                        text = "`${state.server?.attributes?.uuid}`",
                        textAlign = TextAlign.End,
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 200.dp
                        )
                    )
                }

                InfoRow(
                    label = "Owner"
                ) {
                    Text(
                        text = state.server?.attributes?.relationships?.user?.attributes?.username ?: "Unknown",
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .clickable(
                                enabled = state.server != null,
                                onClick = {
                                    val userId = state.server?.attributes?.user

                                    if (userId != null) {
                                        navController.navigate(ApplicationUserScreen(userId))
                                    }
                                }
                            )
                            .shimmerable(
                                enabled = state.server == null,
                                width = 100.dp
                            ),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                InfoRow(
                    label = "Node"
                ) {
                    Text(
                        text = state.server?.attributes?.relationships?.node?.attributes?.name ?: "Unknown",
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .clickable(
                                enabled = state.server != null,
                                onClick = {
                                    val nodeId = state.server?.attributes?.node

                                    if (nodeId != null) {
                                        navController.navigate(ApplicationNodeScreen(nodeId))
                                    }
                                }
                            )
                            .shimmerable(
                                enabled = state.server == null,
                                width = 120.dp
                            ),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                InfoRow(
                    label = "Current Egg"
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withLink(
                                LinkAnnotation.Clickable(
                                    tag = "NEST",
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ),
                                    linkInteractionListener = {
                                        val nestId = state.server?.attributes?.nest

                                        if (nestId != null) {
                                            navController.navigate(ApplicationNestScreen(nestId))
                                        }
                                    }
                                )
                            ) {
                                append(state.server?.attributes?.relationships?.nest?.attributes?.name ?: "Unknown")
                            }

                            append(" :: ")

                            withLink(
                                LinkAnnotation.Clickable(
                                    tag = "EGG",
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ),
                                    linkInteractionListener = {
                                        val nestId = state.server?.attributes?.nest
                                        val eggId = state.server?.attributes?.egg

                                        if (eggId != null && nestId != null) {
                                            navController.navigate(
                                                ApplicationNestEggScreen(
                                                    nestId = nestId,
                                                    eggId = eggId
                                                )
                                            )
                                        }
                                    }
                                )
                            ) {
                                append(state.server?.attributes?.relationships?.egg?.attributes?.name ?: "Unknown")
                            }
                        },
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 190.dp
                        ),
                        textAlign = TextAlign.End,
                    )
                }

                InfoRow(
                    label = "Server Name"
                ) {
                    Text(
                        text = state.server?.attributes?.name ?: "Unknown",
                        textAlign = TextAlign.End,
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 140.dp
                        )
                    )
                }

                InfoRow(
                    label = "CPU Limit"
                ) {
                    CodeText(
                        text = "`${state.server?.attributes?.limits?.cpu}%`",
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 80.dp
                        )
                    )
                }

                InfoRow(
                    label = "CPU Pinning"
                ) {
                    val pinning = state.server?.attributes?.limits?.threads

                    CodeText(
                        text = if (pinning.isNullOrBlank()) {
                            "Not Set"
                        } else {
                            "`${pinning}`"
                        },
                        textAlign = TextAlign.End,
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 120.dp
                        )
                    )
                }

                InfoRow(
                    label = "Memory"
                ) {
                    val memory = (state.server?.attributes?.limits?.memory ?: 0L)

                    val memoryText = if (memory == 0L) {
                        "Unlimited"
                    } else {
                        HumanReadable.fileSize(
                            bytes = memory * 1024L * 1024L,
                            decimals = 2
                        )
                    }

                    CodeText(
                        text = "`$memoryText`",
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 140.dp
                        ),
                        textAlign = TextAlign.End,
                    )
                }

                InfoRow(
                    label = "Swap"
                ) {
                    val swap = (state.server?.attributes?.limits?.swap ?: -1L)

                    val swapText = if (swap == -1L) {
                        "Unlimited"
                    } else {
                        HumanReadable.fileSize(
                            bytes = swap * 1024L * 1024L,
                            decimals = 2
                        )
                    }

                    CodeText(
                        text = "`$swapText`",
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 130.dp
                        ),
                        textAlign = TextAlign.End,
                    )
                }

                InfoRow(
                    label = "Disk Space"
                ) {
                    val disk = (state.server?.attributes?.limits?.disk ?: 0L)

                    val diskText = if (disk == 0L) {
                        "Unlimited"
                    } else {
                        HumanReadable.fileSize(
                            bytes = disk * 1024L * 1024L,
                            decimals = 2
                        )
                    }

                    CodeText(
                        text = "`$diskText`",
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 120.dp
                        )
                    )
                }

                InfoRow(
                    label = "Block IO Weight"
                ) {
                    val io = (state.server?.attributes?.limits?.io ?: 0L)

                    val ioText = if (io == 0L) {
                        "Not Set"
                    } else {
                        "$io"
                    }

                    CodeText(
                        text = "`$ioText`",
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 90.dp
                        )
                    )
                }

                val defaultAllocation = state.server?.attributes?.relationships?.allocations?.data?.find { it.attributes.id == state.server?.attributes?.allocation }

                InfoRow(
                    label = "Default Allocation"
                ) {
                    CodeText(
                        text = if (defaultAllocation == null) {
                            "Unknown"
                        } else {
                            "`${defaultAllocation.attributes.ip}:${defaultAllocation.attributes.port}`"
                        },
                        textAlign = TextAlign.End,
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 120.dp
                        )
                    )
                }

                InfoRow(
                    label = "Connection Alias"
                ) {
                    CodeText(
                        text = if (defaultAllocation == null) {
                            "Unknown"
                        } else if (defaultAllocation.attributes.alias.isNullOrBlank()) {
                            "Not Set"
                        } else {
                            "`${defaultAllocation.attributes.alias}:${defaultAllocation.attributes.port}`"
                        },
                        textAlign = TextAlign.End,
                        modifier = Modifier.shimmerable(
                            enabled = state.server == null,
                            width = 150.dp
                        )
                    )
                }
            }
        }
    }
}